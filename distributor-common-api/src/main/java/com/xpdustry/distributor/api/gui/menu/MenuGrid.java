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

import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MenuGrid {

    static MenuGrid create() {
        return new MenuGridImpl();
    }

    List<List<MenuOption>> getOptions();

    List<MenuOption> getRow(final int index);

    MenuGrid setRow(final int index, final List<MenuOption> options);

    default MenuGrid addRow(final MenuOption... options) {
        return addRow(Arrays.asList(options));
    }

    MenuGrid addRow(final List<MenuOption> options);

    default MenuGrid addRow(final int index, final MenuOption... options) {
        return addRow(index, Arrays.asList(options));
    }

    MenuGrid addRow(final int index, final List<MenuOption> options);

    MenuGrid removeRow(final int index);

    @Nullable MenuOption getOption(final int id);

    MenuOption getOption(final int x, final int y);
}
