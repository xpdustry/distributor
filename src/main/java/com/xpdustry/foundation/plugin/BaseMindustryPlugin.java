// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import arc.ApplicationListener;
import arc.Core;
import arc.files.Fi;
import arc.util.CommandHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import mindustry.Vars;
import mindustry.graphics.MultiPacker;
import mindustry.mod.Plugin;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// Alternative base [Plugin] class for foundation plugins.
///
/// It provides:
/// - Utilities such as [#logger()], [#metadata()], and [#directory()].
/// - Plugin lifecycle management through [PluginListener]. The plugin itself is a listener, and more listeners
///   can be registered with [#addListener(PluginListener)].
/// - A corrected initialization order for server commands. Mindustry calls
///   [Plugin#registerServerCommands(CommandHandler)] before [Plugin#init()], while this class calls [#onInit()] before
///   [#onServerCommandsRegistration(CommandHandler)].
@SuppressWarnings({"DeprecatedIsStillUsed", "this-escape"})
public abstract class BaseMindustryPlugin extends Plugin
        implements ListenablePluginFacade, PluginListener, PluginFacade {

    static {
        Core.app.addListener(new MindustryPluginShutdownHook());
    }

    private final List<PluginListener> listeners = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PluginMetadata metadata = PluginMetadata.from(this.getClass());
    private @Nullable CommandHandler serverCommandHandler = null;

    {
        this.addListener(this);
    }

    /// Returns the plugin data directory
    public final Path directory() {
        return this.getConfigFolder().file().toPath();
    }

    @Override
    public final Logger logger() {
        return this.logger;
    }

    @Override
    public final PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public final List<PluginListener> listeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    @Override
    public <L extends PluginListener> L addListener(final L listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalStateException(listener + " is already added");
        }
        this.listeners.add(listener);
        return listener;
    }

    /// @deprecated Use [#directory()] instead.
    @Deprecated
    @Override
    public final Fi getConfigFolder() {
        return super.getConfigFolder();
    }

    /// @deprecated Use [#directory()] and resolve the desired file explicitly.
    @Deprecated
    @Override
    public final Fi getConfig() {
        return super.getConfig();
    }

    /// @deprecated Implement [#onServerCommandsRegistration(CommandHandler)] instead.
    @Deprecated
    @Override
    public final void registerServerCommands(final CommandHandler handler) {
        this.serverCommandHandler = handler;
    }

    /// @deprecated Implement [#onInit()] instead.
    @Deprecated
    @Override
    public final void init() {
        try {
            Files.createDirectories(this.directory());
        } catch (final IOException e) {
            throw new RuntimeException(
                    "Failed to create data directory for the " + this.metadata.name() + " plugin", e);
        }
        final var handler = Objects.requireNonNull(this.serverCommandHandler);
        this.forEachListener(PluginListener::onInit);
        this.forEachListener(listener -> listener.onServerCommandsRegistration(handler));
        Core.app.addListener(new PluginApplicationListener(this));
    }

    /// @deprecated Implement [#onClientCommandsRegistration(CommandHandler)] instead.
    @Deprecated
    @Override
    public final void registerClientCommands(final CommandHandler handler) {
        this.forEachListener(listener -> listener.onClientCommandsRegistration(handler));
    }

    /// @deprecated Plugins do not use this feature.
    @Deprecated
    @Override
    public final void loadContent() {}

    /// @deprecated Plugins do not use this feature.
    @Deprecated
    @Override
    public final void packSprites(final MultiPacker packer) {}

    private void forEachListener(final Consumer<PluginListener> consumer) {
        this.forEachListener(consumer, true);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void forEachListener(final Consumer<PluginListener> consumer, final boolean ascending) {
        if (ascending) {
            for (int i = 0; i < this.listeners.size(); i++) {
                consumer.accept(this.listeners.get(i));
            }
        } else {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                consumer.accept(this.listeners.get(i));
            }
        }
    }

    private record PluginApplicationListener(BaseMindustryPlugin plugin) implements ApplicationListener {

        @Override
        public void update() {
            this.plugin.forEachListener(PluginListener::onTick);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "{plugin="
                    + this.plugin.metadata().name() + "}";
        }
    }

    // This listener ensures dependent plugins are exited before their dependencies.
    private record MindustryPluginShutdownHook() implements ApplicationListener {

        @Override
        public void dispose() {
            for (final var mod : Vars.mods.orderedMods().copy().reverse()) {
                if (mod.enabled() && mod.main instanceof final BaseMindustryPlugin plugin) {
                    try {
                        plugin.forEachListener(PluginListener::onExit, false);
                    } catch (final Throwable e) {
                        plugin.logger()
                                .error(
                                        "An error occurred while exiting plugin {}",
                                        plugin.metadata().name(),
                                        e);
                    }
                }
            }
        }
    }
}
