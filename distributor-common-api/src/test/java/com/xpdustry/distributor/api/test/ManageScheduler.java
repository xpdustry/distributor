/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.api.test;

import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.common.scheduler.PluginSchedulerImpl;
import com.xpdustry.distributor.common.scheduler.PluginTimeSource;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.ReflectionUtils;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

public final class ManageScheduler
        implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private final Map<Field, PluginSchedulerImpl> schedulers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService updater = Executors.newScheduledThreadPool(1);

    @Override
    public void beforeAll(final ExtensionContext context) {
        updater.scheduleAtFixedRate(
                () -> schedulers.values().forEach(PluginSchedulerImpl::onPluginUpdate),
                16L,
                16L,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        findAnnotatedFields(context.getRequiredTestClass(), TestScheduler.class, ReflectionUtils::isNotStatic)
                .forEach(field -> {
                    if (!PluginScheduler.class.isAssignableFrom(field.getType())) {
                        throw new ExtensionConfigurationException(String.format(
                                "Field [%s] must be of type [%s] to be annotated with @%s.",
                                field, PluginScheduler.class.getName(), TestScheduler.class.getSimpleName()));
                    }

                    try {
                        final var scheduler = new PluginSchedulerImpl(PluginTimeSource.standard(), Runnable::run, 4);
                        ReflectionUtils.makeAccessible(field).set(context.getRequiredTestInstance(), scheduler);
                        schedulers.put(field, scheduler);
                    } catch (final Throwable t) {
                        ExceptionUtils.throwAsUncheckedException(t);
                    }
                });
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        findAnnotatedFields(context.getRequiredTestClass(), TestScheduler.class, ReflectionUtils::isNotStatic)
                .forEach(field -> {
                    final var scheduler =
                            Objects.requireNonNull(schedulers.remove(field), "Scheduler not found for field: " + field);
                    try {
                        if (scheduler != ReflectionUtils.makeAccessible(field).get(context.getRequiredTestInstance())) {
                            throw new ExtensionConfigurationException(
                                    "Scheduler field was replaced during test execution: " + field);
                        }
                    } catch (final IllegalAccessException e) {
                        throw new ExtensionConfigurationException("Scheduler field is not accessible: " + field, e);
                    }
                    scheduler.onPluginExit();
                });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        updater.shutdown();
        updater.awaitTermination(1, TimeUnit.SECONDS);
    }
}
