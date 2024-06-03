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
package com.xpdustry.distributor.api.gui.menu;

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.gui.transform.AbstractTransformerWindowManager;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

final class MenuManagerImpl extends AbstractTransformerWindowManager<MenuManager, MenuPane> implements MenuManager {

    private final int id = Menus.registerMenu(this::handle);

    MenuManagerImpl(final MindustryPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onWindowOpen(final SimpleWindow window) {
        Call.followUpMenu(
                window.getViewer().con(),
                id,
                render(window, window.getPane().getTitle()),
                render(window, window.getPane().getContent()),
                window.getPane().getGrid().getOptions().stream()
                        .map(row -> row.stream()
                                .map(MenuOption::getContent)
                                .map(content -> render(window, content))
                                .toArray(String[]::new))
                        .toArray(String[][]::new));
    }

    @Override
    protected void onWindowClose(final SimpleWindow window) {
        Call.hideFollowUpMenu(window.getViewer().con(), id);
    }

    @Override
    protected MenuPane createPane() {
        return MenuPane.create();
    }

    private void handle(final Player player, final int option) {
        if (isDisposed()) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received menu response from player {} (uuid: {}) but the factory is disposed",
                            player.plainName(),
                            player.uuid());
            return;
        }
        final var window = getWindows().get(MUUID.from(player));
        if (window == null) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received menu response from player {} (uuid: {}) but no window was found",
                            player.plainName(),
                            player.uuid());
        } else if (option == -1) {
            window.getPane().getExitAction().act(window);
        } else {
            final var choice = window.getPane().getGrid().getOption(option);
            if (choice == null) {
                getPlugin()
                        .getLogger()
                        .debug(
                                "Received invalid menu option {} from player {} (uuid: {})",
                                option,
                                player.name(),
                                player.uuid());
            } else {
                choice.getAction().act(window);
            }
        }
    }

    private String render(final Window window, final Component component) {
        return ComponentStringBuilder.mindustry(window.getAudience().getMetadata())
                .append(component)
                .toString();
    }
}
