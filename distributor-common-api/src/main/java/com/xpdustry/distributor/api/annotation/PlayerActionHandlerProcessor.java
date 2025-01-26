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
package com.xpdustry.distributor.api.annotation;

import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import mindustry.Vars;
import mindustry.net.Administration;

final class PlayerActionHandlerProcessor
        extends MethodAnnotationProcessor<PlayerActionHandler, EventSubscription, EventSubscription> {

    private final MindustryPlugin plugin;

    PlayerActionHandlerProcessor(final MindustryPlugin plugin) {
        super(PlayerActionHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected EventSubscription process(
            final Object instance, final Method method, final PlayerActionHandler annotation) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(
                    "The player action handler on " + method + " hasn't the right parameter count.");
        }
        if (!method.getParameters()[0].getType().equals(Administration.PlayerAction.class)) {
            throw new IllegalArgumentException(
                    "The parameter of the player on " + method + " is not a Administration.PlayerAction");
        }
        if (!method.getReturnType().equals(boolean.class)) {
            throw new IllegalArgumentException("The player action handler on " + method + " doesn't return a boolean.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        final var filter = new MethodActionFilter(instance, method, this.plugin);
        Vars.netServer.admins.addActionFilter(filter);
        return () -> Vars.netServer.admins.actionFilters.remove(filter);
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    private record MethodActionFilter(Object target, Method method, MindustryPlugin plugin)
            implements Administration.ActionFilter {

        @Override
        public boolean allow(final Administration.PlayerAction action) {
            try {
                return (boolean) this.method.invoke(this.target, action);
            } catch (final Exception e) {
                this.plugin.getLogger().error("Failed to invoke {} on {}", this.method, this.target, e);
                return true;
            }
        }
    }
}
