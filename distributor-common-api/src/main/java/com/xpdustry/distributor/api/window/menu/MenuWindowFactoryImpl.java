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
package com.xpdustry.distributor.api.window.menu;

import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.window.AbstractTransformerWindowFactory;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

final class MenuWindowFactoryImpl extends AbstractTransformerWindowFactory<MenuWindow> implements MenuWindowFactory {

    private final int id = Menus.registerMenu(this::handle);

    MenuWindowFactoryImpl(final MindustryPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onWindowOpen(final SimpleContext context) {
        Call.followUpMenu(
                context.getViewer().con(),
                id,
                context.getWindow().getTitle(),
                context.getWindow().getContent(),
                context.getWindow().getGrid().getOptions().stream()
                        .map(row ->
                                row.stream().map(MenuWindow.Option::getContent).toArray(String[]::new))
                        .toArray(String[][]::new));
    }

    @Override
    protected void onWindowClose(final SimpleContext context) {
        Call.hideFollowUpMenu(context.getViewer().con(), id);
    }

    @Override
    protected MenuWindow createElement() {
        return new MenuWindowImpl();
    }

    private void handle(final Player player, final int option) {
        if (isDisposed()) {
            return;
        }
        final var context = getContexts().get(MUUID.from(player));
        if (context == null) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received menu response from player {} (uuid: {}) but no context was found",
                            player.plainName(),
                            player.uuid());
        } else if (option == -1) {
            context.getWindow().getExitAction().act(context);
        } else {
            final var choice = context.getWindow().getGrid().getOption(option);
            if (choice == null) {
                getPlugin()
                        .getLogger()
                        .debug(
                                "Received invalid menu option {} from player {} (uuid: {})",
                                option,
                                player.name(),
                                player.uuid());
            } else {
                choice.getAction().act(context);
            }
        }
    }
}
