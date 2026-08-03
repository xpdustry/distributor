// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import com.xpdustry.foundation.plugin.PluginFacade;
import java.time.Duration;

public interface MindustryScheduler {

    /// Returns a new [TaskBuilder] instance for scheduling a task.
    ///
    /// @param plugin the plugin facade to bind the task to
    /// @return a new [TaskBuilder] instance
    TaskBuilder newTaskBuilder(final PluginFacade plugin);

    /// Builds and schedules a [MindustryTask].
    interface TaskBuilder {

        /// Runs the task after a delay.
        ///
        /// Non-negative delays shorter than one tick are normalized to one tick.
        ///
        /// @param duration the delay
        /// @return this builder
        /// @throws IllegalArgumentException if the delay is negative
        default TaskBuilder initialDelay(final Duration duration) {
            if (duration.isNegative()) {
                throw new IllegalArgumentException("The initial delay must not be negative.");
            }
            return this.initialDelay(duration.toMillis(), MindustryTimeUnit.MILLIS);
        }

        /// Runs the task after a delay.
        ///
        /// Non-negative delays shorter than one tick are normalized to one tick.
        ///
        /// @param time the delay
        /// @param unit the time unit of the delay
        /// @return this builder
        /// @throws IllegalArgumentException if the delay is negative
        TaskBuilder initialDelay(final long time, final MindustryTimeUnit unit);

        /// Runs the task periodically with a fixed interval.
        ///
        /// Periodic execution stops if the task throws an exception.
        /// Non-negative delays shorter than one tick are normalized to one tick.
        ///
        /// @param duration the interval between the end of the last execution and the start of the next
        /// @return this builder
        /// @throws IllegalArgumentException if the delay is negative
        default TaskBuilder repeatWithDelay(final Duration duration) {
            if (duration.isNegative()) {
                throw new IllegalArgumentException("The repeat delay must not be negative.");
            }
            return this.repeatWithDelay(duration.toMillis(), MindustryTimeUnit.MILLIS);
        }

        /// Runs the task periodically with a fixed interval.
        ///
        /// Periodic execution stops if the task throws an exception.
        /// Non-negative delays shorter than one tick are normalized to one tick.
        ///
        /// @param time the interval between the end of the last execution and the start of the next
        /// @param unit the time unit of the interval
        /// @return this builder
        /// @throws IllegalArgumentException if the delay is negative
        TaskBuilder repeatWithDelay(final long time, final MindustryTimeUnit unit);

        /// Builds and schedules a task.
        ///
        /// @param action the action to run
        /// @return the scheduled task
        default MindustryTask execute(final Runnable action) {
            return this.execute(_ -> action.run());
        }

        /// Builds and schedules a task.
        ///
        /// @param action the action to run; the supplied [MindustryTask] can cancel itself
        ///         during periodic execution
        /// @return the scheduled task
        MindustryTask execute(final MindustryTaskAction action);
    }
}
