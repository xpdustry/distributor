/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.core.security;

import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.event.EventHandler;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent;
import fr.xpdustry.distributor.api.security.PlayerValidatorEvent.Type;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Players;
import fr.xpdustry.distributor.api.util.Priority;
import fr.xpdustry.distributor.core.DistributorConfiguration;
import fr.xpdustry.distributor.core.DistributorConfiguration.PlayerValidationPolicy;
import mindustry.game.EventType;
import mindustry.gen.Player;

public final class PlayerValidatorListener implements PluginListener {

    private final PlayerValidator playerValidator;
    private final DistributorConfiguration configuration;

    public PlayerValidatorListener(
            final PlayerValidator playerValidator, final DistributorConfiguration configuration) {
        this.playerValidator = playerValidator;
        this.configuration = configuration;
    }

    @EventHandler
    public void onPlayerConnectionConfirmed(final EventType.PlayerConnectionConfirmed event) {
        if (this.configuration.getIdentityValidationPolicy() == PlayerValidationPolicy.VALIDATE_UNKNOWN) {
            if (!this.playerValidator.contains(event.player.uuid())) {
                this.playerValidator.validate(MUUID.of(event.player));
                return;
            }
            if (!this.playerValidator.isValid(MUUID.of(event.player))) {
                // TODO Use dependency injection ? Using the public and the private API is not a good idea.
                event.player.sendMessage(DistributorProvider.get()
                        .getGlobalLocalizationSource()
                        .format("distributor.identity.player.failure", Players.getLocale(event.player)));
            }
        } else if (this.configuration.getIdentityValidationPolicy() == PlayerValidationPolicy.VALIDATE_ALL) {
            this.playerValidator.validate(MUUID.of(event.player));
        }
    }

    // Other listeners must be aware the player is valid, and that is an admin.
    @EventHandler(priority = Priority.HIGH)
    public void onPlayerConnect(final EventType.PlayerConnect event) {
        if (this.configuration.isValidationAutoAdminEnabled() && this.canBeAdmin(event.player)) {
            event.player.admin(true);
        }
    }

    @EventHandler
    public void onPlayerValidatorEvent(final PlayerValidatorEvent event) {
        if (!this.configuration.isValidationAutoAdminEnabled()) {
            return;
        }

        if (event.type() == Type.VALIDATED && this.canBeAdmin(event.player())) {
            event.player().admin(true);
        } else if (event.type() == Type.REMOVED && this.isNotRealAdmin(event.player())) {
            event.player().admin(false);
        } else if (event.type() == Type.INVALIDATED) {
            event.player().admin(false);
        }
    }

    private boolean canBeAdmin(final Player player) {
        return player.getInfo().admin;
    }

    private boolean isNotRealAdmin(final Player player) {
        return player.admin() && !MUUID.of(player).equals(MUUID.of(player.getInfo()));
    }
}
