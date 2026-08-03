// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import com.xpdustry.foundation.plugin.PluginFacade;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MindustrySchedulerImplTest {

    private MutableTimeSource time;
    private PluginFacade plugin;
    private Logger logger;
    private MindustrySchedulerImpl scheduler;

    @BeforeEach
    void before() {
        this.time = new MutableTimeSource();
        this.plugin = mock(PluginFacade.class);
        this.logger = mock(Logger.class);
        when(this.plugin.logger()).thenReturn(this.logger);
        this.scheduler = new MindustrySchedulerImpl(this.time);
    }

    @Test
    void test_simple_schedule() {
        final var executions = new AtomicInteger();
        final var task = this.scheduler.newTaskBuilder(this.plugin).execute(executions::incrementAndGet);

        assertThat(task.state()).isEqualTo(MindustryTask.State.SCHEDULED);
        assertThat(executions).hasValue(0);

        this.scheduler.onTick();

        assertThat(task.state()).isEqualTo(MindustryTask.State.FINISHED);
        assertThat(executions).hasValue(1);
    }

    @Test
    void test_delay() {
        final var executions = new ArrayList<Long>();
        final var task = this.scheduler
                .newTaskBuilder(this.plugin)
                .initialDelay(1L, MindustryTimeUnit.SECONDS)
                .execute(() -> executions.add(this.time.ticks()));

        this.time.advance(59L);
        this.scheduler.onTick();
        assertThat(executions).isEmpty();

        this.time.advance(1L);
        this.scheduler.onTick();

        assertThat(executions).containsExactly(60L);
        assertThat(task.state()).isEqualTo(MindustryTask.State.FINISHED);
    }

    @Test
    void test_interval() {
        final var executionTimes = new ArrayList<Long>();
        final var task = this.scheduler
                .newTaskBuilder(this.plugin)
                .repeatWithDelay(500L, MindustryTimeUnit.MILLIS)
                .execute(() -> executionTimes.add(this.time.ticks()));

        this.scheduler.onTick();
        this.time.advance(30L);
        this.scheduler.onTick();
        this.time.advance(30L);
        this.scheduler.onTick();

        assertThat(executionTimes).containsExactly(0L, 30L, 60L);
        assertThat(task.state()).isEqualTo(MindustryTask.State.SCHEDULED);
    }

    @Test
    void test_cancelling() {
        final var executionTimes = new ArrayList<Long>();
        final var task = this.scheduler
                .newTaskBuilder(this.plugin)
                .repeatWithDelay(Duration.ofMillis(500L))
                .execute(() -> executionTimes.add(this.time.ticks()));

        this.scheduler.onTick();
        this.time.advance(30L);
        this.scheduler.onTick();
        this.time.advance(30L);
        this.scheduler.onTick();
        task.cancel();
        this.time.advance(30L);
        this.scheduler.onTick();

        assertThat(executionTimes).containsExactly(0L, 30L, 60L);
        assertThat(task.state()).isEqualTo(MindustryTask.State.CANCELLED);
    }

    @Test
    void test_initial_delay_is_normalized_to_one_tick() {
        final var executions = new AtomicInteger();
        this.scheduler
                .newTaskBuilder(this.plugin)
                .initialDelay(0L, MindustryTimeUnit.TICKS)
                .execute(executions::incrementAndGet);
        this.scheduler
                .newTaskBuilder(this.plugin)
                .initialDelay(1L, MindustryTimeUnit.MILLIS)
                .execute(executions::incrementAndGet);

        this.scheduler.onTick();
        assertThat(executions).hasValue(0);

        this.time.advance(1L);
        this.scheduler.onTick();
        assertThat(executions).hasValue(2);
    }

    @Test
    void test_repeat_delay_is_normalized_to_one_tick() {
        final var zeroExecutions = new AtomicInteger();
        final var subTickExecutions = new AtomicInteger();
        this.scheduler
                .newTaskBuilder(this.plugin)
                .repeatWithDelay(0L, MindustryTimeUnit.TICKS)
                .execute(zeroExecutions::incrementAndGet);
        this.scheduler
                .newTaskBuilder(this.plugin)
                .repeatWithDelay(1L, MindustryTimeUnit.MILLIS)
                .execute(subTickExecutions::incrementAndGet);

        this.scheduler.onTick();
        assertThat(zeroExecutions).hasValue(1);
        assertThat(subTickExecutions).hasValue(1);

        this.time.advance(1L);
        this.scheduler.onTick();
        assertThat(zeroExecutions).hasValue(2);
        assertThat(subTickExecutions).hasValue(2);
    }

    @Test
    void test_negative_delay() {
        final var builder = this.scheduler.newTaskBuilder(this.plugin);

        assertThatIllegalArgumentException().isThrownBy(() -> builder.initialDelay(-1L, MindustryTimeUnit.TICKS));
        assertThatIllegalArgumentException().isThrownBy(() -> builder.repeatWithDelay(-1L, MindustryTimeUnit.TICKS));
        assertThatIllegalArgumentException().isThrownBy(() -> builder.initialDelay(Duration.ofNanos(-1L)));
        assertThatIllegalArgumentException().isThrownBy(() -> builder.repeatWithDelay(Duration.ofNanos(-1L)));
    }

    @Test
    void test_action_failure() {
        final var failure = new IllegalStateException("boom");
        final var task = this.scheduler.newTaskBuilder(this.plugin).execute(() -> {
            throw failure;
        });

        this.scheduler.onTick();

        assertThat(task.state()).isEqualTo(MindustryTask.State.CANCELLED);
        verify(this.logger).error("An uncaught exception occurred in a plugin scheduler task", failure);
    }

    @Test
    void test_schedule_after_exit() {
        final var executions = new AtomicInteger();
        this.scheduler.onExit();

        final var task = this.scheduler.newTaskBuilder(this.plugin).execute(executions::incrementAndGet);
        this.scheduler.onTick();

        assertThat(task.state()).isEqualTo(MindustryTask.State.CANCELLED);
        assertThat(executions).hasValue(0);
    }

    private static final class MutableTimeSource implements MindustryTimeSource {

        private long ticks;

        @Override
        public long ticks() {
            return this.ticks;
        }

        void advance(final long ticks) {
            this.ticks += ticks;
        }
    }
}
