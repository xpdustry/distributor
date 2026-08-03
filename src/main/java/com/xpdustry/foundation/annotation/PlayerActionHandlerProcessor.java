// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.event.EventSubscription;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.scheduler.MindustryThread;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import mindustry.Vars;
import mindustry.net.Administration;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL, consumers = "com.xpdustry.foundation.annotation.*")
public class PlayerActionHandlerProcessor
        extends MethodAnnotationProcessor<PlayerActionHandler, EventSubscription, EventSubscription> {

    protected final PluginFacade plugin;

    protected PlayerActionHandlerProcessor(final PluginFacade plugin) {
        super(PlayerActionHandler.class);
        this.plugin = plugin;
    }

    @Override
    protected EventSubscription process(
            final Object instance, final Method method, final PlayerActionHandler annotation) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(
                    "The player action handler on " + method + " has the wrong parameter count.");
        }
        if (!method.getParameters()[0].getType().equals(Administration.PlayerAction.class)) {
            throw new IllegalArgumentException("The parameter of the player action handler on " + method
                    + " is not an Administration.PlayerAction.");
        }
        if (!method.getReturnType().equals(boolean.class)) {
            throw new IllegalArgumentException(
                    "The player action handler on " + method + " does not return a boolean.");
        }
        if (!method.canAccess(instance)) {
            method.setAccessible(true);
        }
        MindustryThread.checkIsMainThread("process player action handler");
        final var filter = new MethodActionFilter(instance, method, this.plugin);
        Vars.netServer.admins.addActionFilter(filter);
        return () -> {
            MindustryThread.checkIsMainThread("unsubscribe player action handler");
            Vars.netServer.admins.actionFilters.remove(filter);
        };
    }

    @Override
    protected Optional<EventSubscription> reduce(final List<EventSubscription> results) {
        return results.isEmpty()
                ? Optional.empty()
                : Optional.of(() -> results.forEach(EventSubscription::unsubscribe));
    }

    public record MethodActionFilter(Object target, Method method, PluginFacade plugin)
            implements Administration.ActionFilter {

        @Override
        public boolean allow(final Administration.PlayerAction action) {
            try {
                return (boolean) this.method.invoke(this.target, action);
            } catch (final Exception e) {
                this.plugin.logger().error("Failed to invoke {} on {}", this.method, this.target, e);
                return true;
            }
        }
    }
}
