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
package com.xpdustry.distributor.audience;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.permission.PermissionProvider;
import java.net.URI;
import java.util.Locale;
import mindustry.gen.Call;
import mindustry.gen.Player;

final class PlayerAudience implements Audience {

    private final Player player;
    private final PermissionProvider permissions;
    private final MetadataContainer metadata;

    PlayerAudience(final Player player) {
        this.player = player;
        this.permissions = PermissionProvider.from(player);
        this.metadata = MetadataContainer.builder()
                .putSupplier(Audience.NAME, () -> player.getInfo().plainLastName())
                .putSupplier(Audience.DISPLAY_NAME, player::coloredName)
                .putConstant(Audience.MUUID, com.xpdustry.distributor.api.player.MUUID.from(player))
                .putSupplier(
                        Audience.LOCALE,
                        () -> Locale.forLanguageTag(player.locale().replace('-', '_')))
                .putSupplier(Audience.TEAM, player::team)
                .build();
    }

    @Override
    public void sendMessage(final String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(final Component component) {
        player.sendMessage(DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void sendMessage(final String message, final String unformatted, final Audience sender) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        player.sendMessage(DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void sendWarning(final String message) {
        Call.announce(player.con(), message);
    }

    @Override
    public void sendWarning(final Component component) {
        Call.announce(
                player.con(), DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void showHUDText(final String message) {
        Call.setHudText(player.con(), message);
    }

    @Override
    public void showHUDText(final Component component) {
        Call.setHudText(
                player.con(), DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void hideHUDText() {
        Call.hideHudText(player.con());
    }

    @Override
    public void sendNotification(final String message, final char icon) {
        Call.warningToast(player.con(), icon, message);
    }

    @Override
    public void sendNotification(final Component component, final char icon) {
        Call.warningToast(
                player.con(),
                icon,
                DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void sendAnnouncement(final String message) {
        Call.infoMessage(player.con(), message);
    }

    @Override
    public void sendAnnouncement(final Component component) {
        Call.infoMessage(
                player.con(), DistributorProvider.get().getMindustryEncoder().encode(component, getMetadata()));
    }

    @Override
    public void openURI(final URI uri) {
        Call.openURI(player.con(), uri.toString());
    }

    @Override
    public MetadataContainer getMetadata() {
        return metadata;
    }

    @Override
    public PermissionProvider getPermissions() {
        return permissions;
    }
}
