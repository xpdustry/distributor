// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks a method as a scheduled task handler.
///
/// The method may have one [com.xpdustry.foundation.scheduler.MindustryTask] parameter
/// to allow the task to cancel itself.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScheduledTaskHandler {

    /// The initial delay before the first execution of the task.
    ///
    /// The task is executed immediately if the delay is below `0`.
    long initialDelay();

    /// The delay between the end of the execution of the task and the next invocation.
    ///
    /// The task is executed once if the interval is below `0`.
    long delay();

    /// The time unit of the interval and initial delay.
    MindustryTimeUnit unit();
}
