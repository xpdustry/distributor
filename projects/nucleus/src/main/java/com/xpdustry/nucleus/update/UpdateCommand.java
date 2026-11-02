// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import arc.util.CommandHandler;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.util.concurrent.Executor;

/// The `nupdate` server command, to check for an update and apply it.
public final class UpdateCommand implements PluginListener {

    private final UpdateService updates;
    private final Executor executor;
    private final MindustryScheduler scheduler;
    private final PluginFacade plugin;
    private final PluginLogger logger;

    @Inject
    public UpdateCommand(
            final UpdateService updates,
            final Executor executor,
            final MindustryScheduler scheduler,
            final PluginFacade plugin,
            final PluginLogger logger) {
        this.updates = updates;
        this.executor = executor;
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        handler.register(
                "nupdate",
                "[now]",
                "Check for an update of the server, then restart into it when safe, or right away with now.",
                args -> {
                    final var now = args.length > 0;
                    if (now && !args[0].equals("now")) {
                        this.logger.error("Unknown option {}, expected now", args[0]);
                        return;
                    }
                    this.executor.execute(() -> this.check(now));
                });
    }

    private void check(final boolean now) {
        final UpdateService.Check check;
        try {
            check = this.updates.check();
        } catch (final IOException e) {
            this.logger.error("Failed to check for updates", e);
            return;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        switch (check) {
            case UpdateService.Check.Disabled disabled -> this.logger.info(disabled.reason());
            case UpdateService.Check.UpToDate _ ->
                this.logger.info("No update, the server runs the latest distribution");
            case UpdateService.Check.Staged staged -> {
                if (now) {
                    this.scheduler.newTaskBuilder(this.plugin).execute(this.updates::apply);
                } else {
                    this.logger.info("{} is staged, run nupdate now to restart into it", staged.distribution());
                }
            }
        }
    }
}
