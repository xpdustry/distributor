// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.gatekeeper;

import java.time.Duration;

public sealed interface GatekeeperDecision permits GatekeeperDecision.Allow, GatekeeperDecision.Kick {

    Allow ALLOW = new Allow();

    final class Allow implements GatekeeperDecision {
        private Allow() {}
    }

    record Kick(String reason, Duration duration) implements GatekeeperDecision {

        private static final Duration FIVE_SECONDS = Duration.ofSeconds(5);

        public Kick(final String reason) {
            this(reason, FIVE_SECONDS);
        }
    }
}
