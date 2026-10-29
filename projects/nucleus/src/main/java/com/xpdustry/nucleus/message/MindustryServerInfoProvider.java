// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import arc.Core;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.nucleus.dependency.Inject;
import com.xpdustry.nucleus.network.InetAddressProvider;
import com.xpdustry.nucleus.version.MindustryVersion;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.net.Administration;

final class MindustryServerInfoProvider implements ServerInfoProvider {

    private final InetAddressProvider addresses;

    @Inject
    MindustryServerInfoProvider(final InetAddressProvider addresses) {
        this.addresses = addresses;
    }

    // TODO Unslop Later
    @Override
    public ServerInfo get() {
        final var host = this.addresses.address().getHostAddress();
        if (MindustryThread.isMainThread()) {
            return this.create(host);
        }
        try {
            return CompletableFuture.supplyAsync(() -> this.create(host), Core.app::post)
                    .get(5L, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while reading the server info", e);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to read the server info", e);
        }
    }

    private ServerInfo create(final String host) {
        return new ServerInfo(
                host,
                Administration.Config.port.num(),
                Vars.state.map.name(),
                Administration.Config.desc.string(),
                Vars.state.wave,
                Groups.player.size(),
                Vars.netServer.admins.getPlayerLimit(),
                MindustryVersion.current(),
                ServerInfo.Gamemode.valueOf(Vars.state.rules.mode().name().toUpperCase(Locale.ROOT)),
                Vars.state.rules.modeName,
                switch (Vars.state.getState()) {
                    case playing -> ServerInfo.State.PLAYING;
                    case paused -> ServerInfo.State.PAUSED;
                    case menu -> ServerInfo.State.STOPPED;
                });
    }
}
