// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.lifecycle;

/// Ends the process of the server, for the platform to start it again.
public interface RestartService {

    /// Kicks the players and stops the server, which the platform then starts again, on the main thread.
    void restart();
}
