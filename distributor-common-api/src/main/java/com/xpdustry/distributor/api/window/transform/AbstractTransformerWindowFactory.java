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
package com.xpdustry.distributor.api.window.transform;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginAware;
import com.xpdustry.distributor.api.window.State;
import com.xpdustry.distributor.api.window.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mindustry.game.EventType;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractTransformerWindowFactory<W extends Window>
        implements TransformerWindowFactory<W>, PluginAware {

    private final MindustryPlugin plugin;
    private final EventSubscription playerLeaveListener;
    private final Map<MUUID, SimpleContext> contexts = new HashMap<>();
    private final List<Transformer<W>> transformers = new ArrayList<>();
    private boolean disposed = false;

    protected AbstractTransformerWindowFactory(final MindustryPlugin plugin) {
        this.plugin = plugin;
        this.playerLeaveListener = DistributorProvider.get()
                .getEventBus()
                .subscribe(EventType.PlayerLeave.class, plugin, event -> {
                    final var context = contexts.get(MUUID.from(event.player));
                    if (context != null) context.close();
                });
    }

    protected abstract void onWindowOpen(final SimpleContext context);

    protected void onWindowClose(final SimpleContext context) {}

    protected void onFactoryDispose() {
        playerLeaveListener.unsubscribe();
    }

    protected abstract W createWindow();

    @Override
    public final Window.Context create(final Window.Context parent) {
        return new SimpleContext(parent.getViewer(), parent);
    }

    @Override
    public final Window.Context create(final Player viewer) {
        return new SimpleContext(viewer, null);
    }

    @Override
    public final void addTransformer(final Transformer<W> transformer) {
        transformers.add(transformer);
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return plugin;
    }

    @Override
    public final void dispose() {
        if (!disposed) {
            disposed = true;
            contexts.values().forEach(Window.Context::close);
            onFactoryDispose();
        }
    }

    protected Map<MUUID, SimpleContext> getContexts() {
        return Collections.unmodifiableMap(contexts);
    }

    protected boolean isDisposed() {
        return disposed;
    }

    protected final class SimpleContext implements Window.Context {

        private final Player viewer;
        private final Window.@Nullable Context parent;
        private final State state;
        private @MonotonicNonNull W window = null;
        private boolean transforming = false;

        private SimpleContext(final Player viewer, final Window.@Nullable Context parent) {
            this.viewer = viewer;
            this.parent = parent;
            this.state = parent != null ? parent.getState() : State.create();
        }

        @Override
        public void open() {
            if (AbstractTransformerWindowFactory.this.disposed) {
                return;
            }

            checkNotTransforming();
            final var previous = AbstractTransformerWindowFactory.this.contexts.put(MUUID.from(viewer), this);
            if (previous != null && previous != this) {
                previous.close();
            }

            try {
                this.transforming = true;
                this.window = AbstractTransformerWindowFactory.this.createWindow();
                for (final var transform : transformers) {
                    transform.transform(this.window, this);
                }
            } finally {
                this.transforming = false;
            }

            AbstractTransformerWindowFactory.this.onWindowOpen(this);
        }

        @Override
        public void close() {
            checkNotTransforming();
            if (AbstractTransformerWindowFactory.this.contexts.remove(MUUID.from(viewer), this)) {
                AbstractTransformerWindowFactory.this.onWindowClose(this);
            }
        }

        @Override
        public boolean isOpen() {
            return AbstractTransformerWindowFactory.this.contexts.containsKey(MUUID.from(viewer));
        }

        @Override
        public Player getViewer() {
            return viewer;
        }

        @Override
        public State getState() {
            return state;
        }

        @Override
        public Optional<Window.Context> getParent() {
            return Optional.ofNullable(parent);
        }

        public W getWindow() {
            return this.window;
        }

        private void checkNotTransforming() {
            if (transforming) {
                throw new IllegalStateException("Cannot open or close a window while transforming");
            }
        }
    }
}
