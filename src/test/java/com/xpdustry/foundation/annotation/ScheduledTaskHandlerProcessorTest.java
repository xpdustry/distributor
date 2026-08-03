// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustrySchedulerImpl;
import com.xpdustry.foundation.scheduler.MindustryTimeSource;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"UnusedMethod", "UnusedVariable"})
public final class ScheduledTaskHandlerProcessorTest {

    private MutableTimeSource time;
    private MindustrySchedulerImpl scheduler;
    private ScheduledTaskHandlerProcessor processor;

    @BeforeEach
    void setup() {
        this.time = new MutableTimeSource();
        this.scheduler = new MindustrySchedulerImpl(this.time);
        this.processor = new ScheduledTaskHandlerProcessor(mock(PluginFacade.class), this.scheduler);
    }

    @Test
    void test_simple() {
        final var instance = new TestSimple();
        this.processor.process(instance);

        this.scheduler.onTick();
        assertThat(instance.executions).hasValue(0);

        this.time.advance(1L);
        this.scheduler.onTick();
        this.time.advance(2L);
        this.scheduler.onTick();

        assertThat(instance.executions).hasValue(2);
    }

    @Test
    void test_invalid_parameter() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestInvalidParameter()));
    }

    @Test
    void test_too_many_parameters() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.processor.process(new TestTooManyParameters()));
    }

    private static final class TestSimple {
        private final AtomicInteger executions = new AtomicInteger();

        @ScheduledTaskHandler(initialDelay = 1L, delay = 2L, unit = MindustryTimeUnit.TICKS)
        private void task() {
            this.executions.incrementAndGet();
        }
    }

    private static final class TestInvalidParameter {
        @ScheduledTaskHandler(initialDelay = 1L, delay = 2L, unit = MindustryTimeUnit.TICKS)
        private void task(final String invalid) {}
    }

    private static final class TestTooManyParameters {
        @ScheduledTaskHandler(initialDelay = 1L, delay = 2L, unit = MindustryTimeUnit.TICKS)
        private void task(final String first, final String second) {}
    }

    private static final class MutableTimeSource implements MindustryTimeSource {

        private long ticks;

        @Override
        public long ticks() {
            return this.ticks;
        }

        private void advance(final long ticks) {
            this.ticks += ticks;
        }
    }
}
