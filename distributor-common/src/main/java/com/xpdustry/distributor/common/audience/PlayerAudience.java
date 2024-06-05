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
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.net.NetConnection;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static mindustry.Vars.netServer;

public final class PlayerAudience implements Audience {

    private final Player player;
    private final MetadataContainer metadata;

    PlayerAudience(final Player player) {
        this.player = player;
        this.metadata = MetadataContainer.builder()
                .putSupplier(StandardKeys.NAME, () -> player.getInfo().plainLastName())
                .putSupplier(StandardKeys.DISPLAY_NAME, () -> DistributorProvider.get()
                        .getMindustryComponentDecoder()
                        .decode(player.coloredName()))
                .putConstant(StandardKeys.MUUID, com.xpdustry.distributor.api.player.MUUID.from(player))
                .putSupplier(
                        StandardKeys.LOCALE,
                        () -> Locale.forLanguageTag(player.locale().replace('-', '_')))
                .putSupplier(StandardKeys.TEAM, player::team)
                .build();
    }

    @Override
    public void sendMessage(final String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(final ComponentLike component) {
        player.sendMessage(render(component));
    }

    @Override
    public void sendMessage(final String message, final String unformatted, final Audience sender) {
        if (sender instanceof PlayerAudience other) {
            player.sendMessage(message, other.player, unformatted);
        } else {
            player.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(final ComponentLike component, final ComponentLike unformatted, final Audience sender) {
        if (sender instanceof PlayerAudience other) {
            player.sendMessage(render(component), other.player, render(unformatted));
        } else {
            player.sendMessage(render(component));
        }
    }

    @Override
    public void sendWarning(final String message) {
        Call.announce(getConnection(), message);
    }

    @Override
    public void sendWarning(final ComponentLike component) {
        Call.announce(getConnection(), render(component));
    }

    @Override
    public void showHUDText(final String message) {
        Call.setHudText(getConnection(), message);
    }

    @Override
    public void showHUDText(final ComponentLike component) {
        Call.setHudText(getConnection(), render(component));
    }

    @Override
    public void hideHUDText() {
        Call.hideHudText(getConnection());
    }

    @Override
    public void sendNotification(final String message, final char icon) {
        Call.warningToast(getConnection(), icon, message);
    }

    @Override
    public void sendNotification(final ComponentLike component, final char icon) {
        Call.warningToast(getConnection(), icon, render(component));
    }

    @Override
    public void sendAnnouncement(final String message) {
        Call.infoMessage(getConnection(), message);
    }

    @Override
    public void sendAnnouncement(final ComponentLike component) {
        Call.infoMessage(getConnection(), render(component));
    }

    @Override
    public void openURI(final URI uri) {
        Call.openURI(getConnection(), uri.toString());
    }

    @Override
    public void showLabel(final String label, final float x, final float y, final Duration duration) {
        Call.label(getConnection(), label, duration.toMillis() / 1000F, x, y);
    }

    @Override
    public void showLabel(final ComponentLike label, final float x, final float y, final Duration duration) {
        Call.label(getConnection(), render(label), duration.toMillis() / 1000F, x, y);
    }

    @Override
    public void kick(final String reason, final Duration duration, final boolean silent) {
        kick0(reason, duration, silent);
    }

    @Override
    public void kick(final ComponentLike reason, final Duration duration, final boolean silent) {
        kick0(render(reason), duration, silent);
    }

    private void kick0(final String reason, final Duration duration, final boolean silent) {
        final var connection = getConnection();
        if (connection.kicked) return;

        LoggerFactory.getLogger("ROOT")
                .atLevel(silent ? Level.TRACE : Level.INFO)
                .setMessage("Kicking connection {} / {}; Reason: {}")
                .addArgument(connection.address)
                .addArgument(connection.uuid)
                .addArgument(reason.replace("\n", " "))
                .log();

        if (duration.toMillis() > 0L) {
            netServer.admins.handleKicked(connection.uuid, connection.address, duration.toMillis());
        }

        Call.kick(connection, reason);

        if (connection.uuid.startsWith("steam:")) {
            // run with a 2-frame delay so there is time to send the kick packet, steam handles this weirdly
            Core.app.post(() -> Core.app.post(connection::close));
        } else {
            connection.close();
        }

        Vars.netServer.admins.save();
        connection.kicked = true;
    }

    @Override
    public MetadataContainer getMetadata() {
        return metadata;
    }

    @Override
    public PermissionContainer getPermissions() {
        return DistributorProvider.get().getPlayerPermissionProvider().getPermissions(this.player);
    }

    private String render(final ComponentLike component) {
        return ComponentStringBuilder.mindustry(getMetadata())
                .append(component.asComponent())
                .toString();
    }

    private NetConnection getConnection() {
        return Objects.requireNonNull(player.con(), "Player connection is null");
    }
}
