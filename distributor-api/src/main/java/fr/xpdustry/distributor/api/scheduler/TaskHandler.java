/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a task handler, meaning it will be registered and called as a scheduled task in the
 * {@link PluginScheduler}.
 * <br>
 * The annotated method can have one {@link Cancellable} parameter to allow the task to cancel itself.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TaskHandler {

    /**
     * The interval between each execution of the task.
     */
    long interval() default -1;

    /**
     * The initial delay before the first execution of the task.
     */
    long delay() default -1;

    /**
     * The time unit of the interval and initial delay.
     */
    MindustryTimeUnit unit() default MindustryTimeUnit.SECONDS;

    /**
     * Whether the task should be executed asynchronously.
     */
    boolean async() default false;
}