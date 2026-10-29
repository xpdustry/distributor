// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import com.xpdustry.nucleus.version.MindustryVersion;
import org.jspecify.annotations.Nullable;

/// The public state of a server, shared through the [Messenger] discovery.
public record ServerInfo(
        String host,
        int port,
        String mapName,
        String description,
        int wave,
        int playerCount,
        int playerLimit,
        MindustryVersion gameVersion,
        Gamemode gamemode,
        @Nullable String gamemodeName,
        State state) {

    public enum State {
        PLAYING,
        PAUSED,
        STOPPED,
    }

    public enum Gamemode {
        SURVIVAL,
        SANDBOX,
        ATTACK,
        PVP,
        EDITOR,
    }
}
