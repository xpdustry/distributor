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
package com.xpdustry.catalina.common.gui;

import arc.Events;
import arc.struct.IntMap;
import arc.struct.IntQueue;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.player.MUUID;
import java.util.Objects;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class MenuWindow extends BaseTransformerWindow<MenuPane, MenuWindow> {

    private static final ActiveMenuWindowManager MANAGER = new ActiveMenuWindowManager();

    private int identifier = -1;

    MenuWindow(final MutableWindow<?> parent) {
        super(parent);
    }

    @Override
    public boolean active() {
        return MANAGER.listeners.containsKey(this.identifier);
    }

    @Override
    public void show() {
        MANAGER.onShow(this);
    }

    @Override
    public void hide() {
        MANAGER.onHide(this);
    }

    static final class ActiveMenuWindowManager {

        private static final Logger logger = LoggerFactory.getLogger(ActiveMenuWindowManager.class);

        private final IntQueue available = new IntQueue();
        private final IntMap<MenuWindowListener> listeners = new IntMap<>();

        {
            // TODO May not be the most efficient solution if hundreds of active menus
            Events.on(EventType.PlayerLeave.class, (event) -> {
                for (final var listener : this.listeners.values()) {
                    if (listener.window != null && listener.isViewer(event.player)) {
                        listener.window.hide();
                    }
                }
            });
        }

        void onShow(final MenuWindow window) {
            if (window.viewer().getPlayer().con().hasDisconnected) {
                return;
            }
            if (!this.listeners.containsKey(window.identifier)) {
                final MenuWindowListener listener;
                int identifier;
                if (this.available.isEmpty()) {
                    listener = new MenuWindowListener();
                    identifier = Menus.registerMenu(listener);
                    this.listeners.put(identifier, listener);
                    logger.trace("Created new MenuWindowListener with id {}", identifier);
                } else {
                    identifier = this.available.removeFirst();
                    listener = Objects.requireNonNull(this.listeners.get(identifier));
                    logger.trace("Recycled MenuWindowListener with id {}", identifier);
                }
                listener.window = window;
                window.identifier = identifier;
            }
            this.render(window);
        }

        @SuppressWarnings("ConstantValue") // Tf you mean listener is never null...
        void onHide(final MenuWindow window) {
            final var listener = this.listeners.get(window.identifier);
            if (listener != null && listener.window == window) {
                this.available.addLast(window.identifier);
                listener.window = null;
                logger.trace("Freed MenuWindowListener with id {}", window.identifier);
            }
            window.identifier = -1;
        }

        private void render(final MenuWindow window) {
            logger.trace("Rendering MenuWindow with id {}", window.identifier);
            final var options = pane.grid().getOptions().stream()
                    .map(row -> row.stream()
                            .map(MenuOption::getContent)
                            .map(content -> this.render(window, content))
                            .toArray(String[]::new))
                    .toArray(String[][]::new);
            for (int i = 0; i < options.length; i++) {
                if (options[i].length == 0) {
                    throw new IllegalArgumentException("Row " + i + " is empty");
                }
            }
            Call.followUpMenu(
                    window.viewer().getPlayer().con(),
                    window.identifier,
                    this.render(window, pane.title()),
                    this.render(window, pane.content()),
                    options);
        }

        private String render(final MenuWindow window, final Component component) {
            return ComponentStringBuilder.mindustry(window.viewer().getMetadata())
                    .append(component)
                    .toString();
        }

        private static final class MenuWindowListener implements Menus.MenuListener {

            private static final Logger logger = LoggerFactory.getLogger(MenuWindowListener.class);

            private @Nullable MenuWindow window = null;

            @Override
            public void get(final Player player, final int option) {
                final var window = this.window;
                if (window == null) {
                    logger.warn(
                            "Received unknown menu response from player {} (uuid: {})",
                            player.plainName(),
                            player.uuid());
                } else if (!this.isViewer(player)) {
                    logger.warn(
                            "Received unauthorized menu response from player {} (uuid: {})",
                            player.plainName(),
                            player.uuid());
                } else if (option == -1) {
                    window.pane.getExitAction().act(window);
                } else {
                    final var choice = window.pane.getGrid().getOption(option);
                    if (choice == null) {
                        logger.warn(
                                "Received invalid menu option {} from player {} (uuid: {})",
                                option,
                                player.name(),
                                player.uuid());
                    } else {
                        choice.getAction().act(window);
                    }
                }
            }

            public boolean isViewer(final Player player) {
                return this.window != null
                        && MUUID.from(this.window.viewer().getPlayer()).equals(MUUID.from(player));
            }
        }
    }
}
