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
package com.xpdustry.distributor.api.gui.input;

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentAppendable;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.gui.transform.AbstractTransformerWindowManager;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.util.HashSet;
import java.util.Set;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TextInputManagerImpl extends AbstractTransformerWindowManager<TextInputManager, TextInputPane>
        implements TextInputManager {

    private final int id = Menus.registerTextInput(this::handle);
    private final Set<MUUID> visible = new HashSet<>();

    TextInputManagerImpl(final MindustryPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onWindowOpen(final SimpleWindow window) {
        if (this.visible.add(MUUID.from(window.getViewer()))) {
            Call.textInput(
                    window.getViewer().con(),
                    this.id,
                    render(window, window.getPane().getTitle()),
                    render(window, window.getPane().getDescription()),
                    window.getPane().getMaxLength(),
                    render(window, window.getPane().getPlaceholder()),
                    false);
        }
    }

    @Override
    protected TextInputPane createPane() {
        return TextInputPane.create();
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
        final var window = getWindows().get(MUUID.from(player));
        if (window == null) {
            this.getPlugin()
                    .getLogger()
                    .debug(
                            "Received text input from player {} (uuid: {}) but no window was found",
                            player.plainName(),
                            player.uuid());
            return;
        }

        // Simple trick to not reopen an interface when an action already does it.
        visible.remove(MUUID.from(player));
        if (input == null) {
            window.getPane().getExitAction().act(window);
        } else if (input.length() > window.getPane().getMaxLength()) {
            this.getPlugin()
                    .getLogger()
                    .warn(
                            "Received text input from player {} (uuid: {}) with length {} but the maximum length is {}",
                            player.plainName(),
                            player.uuid(),
                            input.length(),
                            window.getPane().getMaxLength());
            window.hide();
        } else {
            window.getPane().getInputAction().act(window, input);
        }
        // The text input closes automatically when the player presses enter,
        // so reopen if it was not explicitly closed by the server.
        if (window.isActive() && !visible.contains(MUUID.from(player))) {
            window.show();
        }
    }

    private String render(final Window window, final Component component) {
        return ComponentAppendable.mindustry(window.getAudience().getMetadata())
                .append(component)
                .toString();
    }
}
