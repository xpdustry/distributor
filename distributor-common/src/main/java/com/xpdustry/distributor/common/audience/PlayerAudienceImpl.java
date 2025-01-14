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

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.key.DynamicKeyContainer;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import com.xpdustry.distributor.api.player.MUUID;
import java.util.Locale;
import java.util.Objects;
import mindustry.gen.Player;

public final class PlayerAudienceImpl extends BaseNetConnectionAudience implements PlayerAudience {

    private final Player player;
    private final KeyContainer metadata;

    PlayerAudienceImpl(final Player player) {
        super(Objects.requireNonNull(player.con(), "Player connection is null"));
        this.player = player;
        this.metadata = DynamicKeyContainer.builder()
                .putConstant(StandardKeys.NAME, player.getInfo().plainLastName())
                .putConstant(
                        StandardKeys.DECORATED_NAME,
                        Distributor.get().getMindustryComponentDecoder().decode(player.getInfo().lastName))
                .putConstant(StandardKeys.MUUID, MUUID.from(player))
                .putSupplied(
                        StandardKeys.LOCALE,
                        () -> Locale.forLanguageTag(player.locale().replace('_', '-')))
                .putSupplied(StandardKeys.TEAM, player::team)
                .putSupplied(StandardKeys.COLOR, () -> ComponentColor.from(player.color()))
                .build();
    }

    @Override
    public KeyContainer getMetadata() {
        return this.metadata;
    }

    @Override
    public PermissionContainer getPermissions() {
        return Distributor.get().getPlayerPermissionProvider().getPermissions(this.player);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
