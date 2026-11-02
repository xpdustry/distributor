// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.foundation.annotation.EventHandler;
import com.xpdustry.foundation.annotation.ScheduledTaskHandler;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import com.xpdustry.nucleus.concurrent.Async;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.time.Duration;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;

/// Checks for updates in the background, and applies the staged one when no game is interrupted: right away on an
/// empty server, otherwise once the game is over or the last player left.
public final class UpdateScheduler implements PluginListener {

    private static final Duration GAME_OVER_DELAY = Duration.ofSeconds(10);

    private final UpdateService updates;
    private final PluginFacade plugin;
    private final MindustryScheduler scheduler;
    private final PluginLogger logger;

    @Inject
    public UpdateScheduler(
            final UpdateService updates,
            final PluginFacade plugin,
            final MindustryScheduler scheduler,
            final PluginLogger logger) {
        this.updates = updates;
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.logger = logger;
    }

    @ScheduledTaskHandler(initialDelay = 1, delay = 5, unit = MindustryTimeUnit.MINUTES)
    @Async
    void onUpdateCheck() {
        try {
            this.updates.check();
        } catch (final IncompatibleUpdateException e) {
            this.logger.warn(String.valueOf(e.getMessage()));
        } catch (final IOException e) {
            this.logger.warn("Failed to check for updates", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @EventHandler
    void onUpdateStaged(final UpdateStagedEvent event) {
        if (Groups.player.isEmpty()) {
            this.updates.apply();
        } else {
            Call.sendMessage("[accent]An update is ready, the server will restart at the end of this game.");
        }
    }

    @EventHandler
    void onGameOver(final EventType.GameOverEvent event) {
        if (this.updates.staged().isPresent()) {
            this.scheduler
                    .newTaskBuilder(this.plugin)
                    .initialDelay(GAME_OVER_DELAY)
                    .execute(this.updates::apply);
        }
    }

    @EventHandler
    void onPlayerLeave(final EventType.PlayerLeave event) {
        // The player is still in the group during the event
        if (this.updates.staged().isPresent() && Groups.player.size() <= 1) {
            this.updates.apply();
        }
    }
}
