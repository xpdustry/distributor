/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.common.audience;

import arc.Core;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import java.net.URI;
import java.time.Duration;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.net.NetConnection;
import mindustry.net.Packets;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class BaseNetConnectionAudience implements Audience {

    protected final NetConnection connection;

    public BaseNetConnectionAudience(final NetConnection connection) {
        this.connection = connection;
    }

    @Override
    public void sendMessage(final Component component) {
        Call.sendMessage(this.connection, render(component), null, null);
    }

    @Override
    public void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        if (sender instanceof PlayerAudience other) {
            Call.sendMessage(this.connection, render(component), render(unformatted), other.getPlayer());
        } else {
            Call.sendMessage(this.connection, render(component), render(unformatted), null);
        }
    }

    @Override
    public void sendWarning(final Component component) {
        Call.announce(this.connection, render(component));
    }

    @Override
    public void showHUDText(final Component component) {
        Call.setHudText(this.connection, render(component));
    }

    @Override
    public void hideHUDText() {
        Call.hideHudText(this.connection);
    }

    @Override
    public void sendNotification(final Component component, final char icon) {
        Call.warningToast(this.connection, icon, render(component));
    }

    @Override
    public void sendAnnouncement(final Component component) {
        Call.infoMessage(this.connection, render(component));
    }

    @Override
    public void openURI(final URI uri) {
        Call.openURI(this.connection, uri.toString());
    }

    @Override
    public void showLabel(final Component label, final float x, final float y, final Duration duration) {
        Call.label(this.connection, render(label), duration.toMillis() / 1000F, x, y);
    }

    @Override
    public void kick(final Component reason, final Duration duration, final boolean log) {
        this.kick(Packets.KickReason.kick, render(reason).replace('\n', ' '), duration.toMillis(), log);
    }

    @Override
    public void kick(final Packets.KickReason reason, final Duration duration, final boolean log) {
        this.kick(reason, reason.name(), duration.toMillis(), log);
    }

    private void kick(
            final Packets.@Nullable KickReason reason, final String details, final long duration, final boolean log) {
        if (this.connection.kicked) return;

        LoggerFactory.getLogger("ROOT")
                .atLevel(log ? Level.INFO : Level.DEBUG)
                .setMessage("Kicking connection {} / {}; Reason: {}")
                .addArgument(this.connection.address)
                .addArgument(this.connection.uuid)
                .addArgument(details)
                .log();

        if (duration > 0) {
            Vars.netServer.admins.handleKicked(this.connection.uuid, this.connection.address, duration);
        }

        if (reason != null) {
            Call.kick(this.connection, reason);
        } else {
            Call.kick(this.connection, details);
        }

        if (this.connection.uuid.startsWith("steam:")) {
            // run with a 2-frame delay so there is time to send the kick packet, steam handles this weirdly
            Core.app.post(() -> Core.app.post(this.connection::close));
        } else {
            this.connection.close();
        }

        Vars.netServer.admins.save();
        this.connection.kicked = true;
    }

    protected String render(final Component component) {
        return ComponentStringBuilder.mindustry(getMetadata()).append(component).toString();
    }
}
