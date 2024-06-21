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

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.key.DynamicKeyContainer;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import com.xpdustry.distributor.api.player.MUUID;
import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.net.NetConnection;

public final class PlayerAudienceImpl implements PlayerAudience {

    private final Player player;
    private final KeyContainer metadata;

    PlayerAudienceImpl(final Player player) {
        this.player = player;
        this.metadata = DynamicKeyContainer.builder()
                .putSupplied(StandardKeys.NAME, () -> player.getInfo().plainLastName())
                .putSupplied(StandardKeys.DISPLAY_NAME, () -> DistributorProvider.get()
                        .getMindustryComponentDecoder()
                        .decode(player.coloredName()))
                .putConstant(StandardKeys.MUUID, MUUID.from(player))
                .putSupplied(
                        StandardKeys.LOCALE,
                        () -> Locale.forLanguageTag(player.locale().replace('-', '_')))
                .putSupplied(StandardKeys.TEAM, player::team)
                .build();
    }

    @Override
    public void sendMessage(final Component component) {
        player.sendMessage(render(component));
    }

    @Override
    public void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        if (sender instanceof PlayerAudience other) {
            player.sendMessage(render(component), other.getPlayer(), render(unformatted));
        } else {
            player.sendMessage(render(component));
        }
    }

    @Override
    public void sendWarning(final Component component) {
        Call.announce(getConnection(), render(component));
    }

    @Override
    public void showHUDText(final Component component) {
        Call.setHudText(getConnection(), render(component));
    }

    @Override
    public void hideHUDText() {
        Call.hideHudText(getConnection());
    }

    @Override
    public void sendNotification(final Component component, final char icon) {
        Call.warningToast(getConnection(), icon, render(component));
    }

    @Override
    public void sendAnnouncement(final Component component) {
        Call.infoMessage(getConnection(), render(component));
    }

    @Override
    public void openURI(final URI uri) {
        Call.openURI(getConnection(), uri.toString());
    }

    @Override
    public void showLabel(final Component label, final float x, final float y, final Duration duration) {
        Call.label(getConnection(), render(label), duration.toMillis() / 1000F, x, y);
    }

    @Override
    public void kick(final Component reason, final Duration duration) {
        getConnection().kick(render(reason), duration.toMillis());
    }

    @Override
    public KeyContainer getMetadata() {
        return metadata;
    }

    @Override
    public PermissionContainer getPermissions() {
        return DistributorProvider.get().getPlayerPermissionProvider().getPermissions(this.player);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    private String render(final Component component) {
        return ComponentStringBuilder.mindustry(getMetadata()).append(component).toString();
    }

    private NetConnection getConnection() {
        return Objects.requireNonNull(player.con(), "Player connection is null");
    }
}
