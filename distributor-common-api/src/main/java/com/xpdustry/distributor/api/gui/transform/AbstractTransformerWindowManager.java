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
package com.xpdustry.distributor.api.gui.transform;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.gui.Pane;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.key.MutableKeyContainer;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginAware;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mindustry.game.EventType;
import mindustry.gen.Player;
import org.jspecify.annotations.Nullable;

/**
 * An abstract implementation of a {@link TransformerWindowManager}.
 *
 * @param <P> the pane type
 */
public abstract class AbstractTransformerWindowManager<P extends Pane>
        implements TransformerWindowManager<P>, PluginAware {

    private final MindustryPlugin plugin;
    private final EventSubscription playerLeaveListener;
    private final Map<MUUID, SimpleWindow> windows = new HashMap<>();
    private final List<Transformer<P>> transformers = new ArrayList<>();
    private boolean disposed = false;

    protected AbstractTransformerWindowManager(final MindustryPlugin plugin) {
        this.plugin = plugin;
        this.playerLeaveListener = Distributor.get()
                .getEventBus()
                .subscribe(EventType.PlayerLeave.class, plugin, event -> {
                    final var window = this.windows.get(MUUID.from(event.player));
                    if (window != null) window.hide();
                });
    }

    protected abstract void onWindowOpen(final SimpleWindow window);

    protected void onWindowClose(final SimpleWindow window) {}

    protected void onDispose() {
        this.playerLeaveListener.unsubscribe();
    }

    protected abstract P createPane();

    @Override
    public final Window create(final Window parent) {
        return new SimpleWindow(parent.getViewer(), parent);
    }

    @Override
    public final Window create(final Player viewer) {
        return new SimpleWindow(viewer, null);
    }

    @Override
    public final Collection<Window> getActiveWindows() {
        return Collections.unmodifiableCollection(this.windows.values());
    }

    @Override
    public @Nullable Window getActiveWindow(final Player viewer) {
        return this.windows.get(MUUID.from(viewer));
    }

    @Override
    public final void addTransformer(final Transformer<P> transformer) {
        this.transformers.add(Objects.requireNonNull(transformer));
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public final void dispose() {
        if (!this.disposed) {
            this.disposed = true;
            this.getActiveWindows().forEach(Window::hide);
            this.onDispose();
        }
    }

    protected final Map<MUUID, SimpleWindow> getWindows() {
        return Collections.unmodifiableMap(this.windows);
    }

    protected final boolean isDisposed() {
        return this.disposed;
    }

    protected final class SimpleWindow implements Window {

        private final Player viewer;
        private final @Nullable Window parent;
        private final MutableKeyContainer state;
        private @Nullable P pane = null;
        private boolean transforming = false;

        private SimpleWindow(final Player viewer, final @Nullable Window parent) {
            this.viewer = viewer;
            this.parent = parent;
            this.state = parent != null ? parent.getState() : MutableKeyContainer.create();
        }

        @Override
        public void show() {
            if (AbstractTransformerWindowManager.this.disposed) {
                return;
            }

            this.checkNotTransforming();
            final var previous = AbstractTransformerWindowManager.this.windows.put(MUUID.from(this.viewer), this);
            if (previous != null && previous != this) {
                previous.hide();
            }

            try {
                this.transforming = true;
                this.pane = AbstractTransformerWindowManager.this.createPane();
                final var context = Transformer.Context.of(this.pane, this.state, this.viewer);
                for (final var transform : AbstractTransformerWindowManager.this.transformers) {
                    transform.transform(context);
                }
            } finally {
                this.transforming = false;
            }

            AbstractTransformerWindowManager.this.onWindowOpen(this);
        }

        @Override
        public void hide() {
            this.checkNotTransforming();
            if (AbstractTransformerWindowManager.this.windows.remove(MUUID.from(this.viewer), this)) {
                AbstractTransformerWindowManager.this.onWindowClose(this);
            }
        }

        @Override
        public boolean isActive() {
            return AbstractTransformerWindowManager.this.windows.containsKey(MUUID.from(this.viewer));
        }

        @Override
        public Player getViewer() {
            return this.viewer;
        }

        @Override
        public MutableKeyContainer getState() {
            return this.state;
        }

        @Override
        public @Nullable Window getParent() {
            return this.parent;
        }

        public P getPane() {
            return Objects.requireNonNull(this.pane);
        }

        private void checkNotTransforming() {
            if (this.transforming) {
                throw new IllegalStateException("Cannot open or close a window while transforming");
            }
        }
    }
}
