// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

/// Where the updater looks for new builds of the monorepo plugins.
public enum UpdateChannel {
    OFF,
    RELEASE,
    /// The snapshots, published for every commit on master.
    SNAPSHOT
}
