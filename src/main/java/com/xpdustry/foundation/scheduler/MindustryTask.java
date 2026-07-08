// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

/// A task scheduled by [MindustryScheduler].
public interface MindustryTask {

    /// Returns the current state of this task.
    State state();

    /// Cancels this task if it is still scheduled.
    void cancel();

    /// The lifecycle state of a [MindustryTask].
    enum State {
        /// The task is waiting to run or will run again.
        SCHEDULED,
        /// The task completed and will not run again.
        FINISHED,
        /// The task was canceled before completing.
        CANCELLED
    }
}
