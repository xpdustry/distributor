// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

/// Provides the [ServerInfo] of this server, shared by the [Messenger] heartbeats.
@FunctionalInterface
public interface ServerInfoProvider {

    /// Returns the current [ServerInfo], may be called from any thread.
    ServerInfo get();
}
