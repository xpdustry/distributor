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
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.gui.Action;
import java.util.Objects;

record MenuOptionImpl(Component content, Action action) implements MenuOption {

    static final MenuOptionImpl EMPTY = new MenuOptionImpl(TextComponent.empty(), Action.none());

    MenuOptionImpl {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(action, "action");
    }

    @Override
    public Component getContent() {
        return this.content;
    }

    @Override
    public Action getAction() {
        return this.action;
    }
}
