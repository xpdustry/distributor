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
import java.util.Objects;
import java.util.StringJoiner;

final class MenuPaneImpl implements MenuPane {

    private String title = "";
    private String content = "";
    private Action exitAction = Action.back();
    private MenuGrid grid = MenuGrid.create();

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public Action getExitAction() {
        return exitAction;
    }

    @Override
    public void setExitAction(final Action exitAction) {
        this.exitAction = exitAction;
    }

    @Override
    public MenuGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(final MenuGrid grid) {
        this.grid = grid;
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o)
                || (o instanceof MenuPaneImpl other
                        && Objects.equals(title, other.title)
                        && Objects.equals(content, other.content)
                        && Objects.equals(exitAction, other.exitAction)
                        && Objects.equals(grid, other.grid));
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, exitAction, grid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MenuPaneImpl.class.getSimpleName() + "{", "}")
                .add("title='" + title + "'")
                .add("content='" + content + "'")
                .add("exitAction=" + exitAction)
                .add("grid=" + grid)
                .toString();
    }
}
