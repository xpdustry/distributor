// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import com.xpdustry.foundation.plugin.PluginFacade;
import java.time.Duration;
import java.util.function.Consumer;

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
        /// @param duration the delay
        /// @return this builder
        default TaskBuilder initialDelay(final Duration duration) {
            return this.initialDelay(duration.toMillis(), MindustryTimeUnit.MILLIS);
        }

        /// Runs the task after a delay.
        ///
        /// @param time the delay
        /// @param unit the time unit of the delay
        /// @return this builder
        TaskBuilder initialDelay(final long time, final MindustryTimeUnit unit);

        /// Runs the task periodically with a fixed interval.
        ///
        /// Periodic execution stops if the task throws an exception.
        ///
        /// @param duration the interval between the end of the last execution and the start of the next
        /// @return this builder
        default TaskBuilder repeatWithDelay(final Duration duration) {
            return this.repeatWithDelay(duration.toMillis(), MindustryTimeUnit.MILLIS);
        }

        /// Runs the task periodically with a fixed interval.
        ///
        /// Periodic execution stops if the task throws an exception.
        ///
        /// @param time the interval between the end of the last execution and the start of the next
        /// @param unit the time unit of the interval
        /// @return this builder
        TaskBuilder repeatWithDelay(final long time, final MindustryTimeUnit unit);

        /// Builds and schedules a task.
        ///
        /// @param runnable the task to run
        /// @return the scheduled task
        default MindustryTask execute(final Runnable runnable) {
            return this.execute(_ -> runnable.run());
        }

        /// Builds and schedules a task.
        ///
        /// @param runnable the task to run; the supplied [MindustryTask] can cancel itself
        ///         during periodic execution
        /// @return the scheduled task
        MindustryTask execute(final Consumer<MindustryTask> runnable);
    }
}
