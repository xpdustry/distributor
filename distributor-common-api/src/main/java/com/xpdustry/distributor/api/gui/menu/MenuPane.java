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

import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.Pane;

public interface MenuPane extends Pane {

    static MenuPane create() {
        return new MenuPaneImpl();
    }

    String getTitle();

    void setTitle(final String title);

    String getContent();

    void setContent(final String content);

    Action getExitAction();

    void setExitAction(final Action action);

    MenuGrid getGrid();

    void setGrid(final MenuGrid grid);
}
