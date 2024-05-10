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
package com.xpdustry.distributor.api.window.input;

import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.window.transform.AbstractTransformerWindowFactory;
import java.util.HashSet;
import java.util.Set;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextInputWindowFactoryImpl extends AbstractTransformerWindowFactory<TextInputWindow>
        implements TextInputWindowFactory {

    private final int id = Menus.registerTextInput(this::handle);
    private final Set<MUUID> visible = new HashSet<>();

    TextInputWindowFactoryImpl(final MindustryPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onWindowOpen(final SimpleContext context) {
        if (this.visible.add(MUUID.from(context.getViewer()))) {
            Call.textInput(
                    context.getViewer().con(),
                    this.id,
                    context.getWindow().getTitle(),
                    context.getWindow().getDescription(),
                    context.getWindow().getMaxLength(),
                    context.getWindow().getPlaceholder(),
                    false);
        }
    }

    @Override
    protected TextInputWindow createWindow() {
        return new TextInputWindowImpl();
    }

    private void handle(final Player player, final @Nullable String input) {
        if (isDisposed()) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received text input from player {} (uuid: {}) but the factory is disposed",
                            player.plainName(),
                            player.uuid());
            return;
        }
        final var context = getContexts().get(MUUID.from(player));
        if (context == null) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received text input from player {} (uuid: {}) but no context was found",
                            player.plainName(),
                            player.uuid());
            return;
        }

        // Simple trick to not reopen an interface when an action already does it.
        visible.remove(MUUID.from(player));
        if (input == null) {
            context.getWindow().getExitAction().act(context);
        } else if (input.length() > context.getWindow().getMaxLength()) {
            this.getPlugin()
                    .getLogger()
                    .warn(
                            "Received text input from player {} (uuid: {}) with length {} but the maximum length is {}",
                            player.plainName(),
                            player.uuid(),
                            input.length(),
                            context.getWindow().getMaxLength());
            context.close();
        } else {
            context.getWindow().getInputAction().act(context, input);
        }
        // The text input closes automatically when the player presses enter,
        // so reopen if it was not explicitly closed by the server.
        if (context.isOpen() && !visible.contains(MUUID.from(player))) {
            context.open();
        }
    }
}
