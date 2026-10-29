// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.scheduler;

/// An action performed by a scheduled [MindustryTask].
@FunctionalInterface
public interface MindustryTaskAction {

    /// Runs this action for the given task.
    ///
    /// @param task the task executing this action
    void run(MindustryTask task);
}
