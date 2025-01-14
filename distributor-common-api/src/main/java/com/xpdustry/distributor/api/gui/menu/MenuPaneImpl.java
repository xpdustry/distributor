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
import java.util.StringJoiner;

final class MenuPaneImpl implements MenuPane {

    private Component title = TextComponent.empty();
    private Component content = TextComponent.empty();
    private Action exitAction = Action.back();
    private MenuGrid grid = MenuGrid.create();

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public MenuPane setTitle(final Component title) {
        this.title = title;
        return this;
    }

    @Override
    public Component getContent() {
        return this.content;
    }

    @Override
    public MenuPane setContent(final Component content) {
        this.content = content;
        return this;
    }

    @Override
    public Action getExitAction() {
        return this.exitAction;
    }

    @Override
    public MenuPane setExitAction(final Action exitAction) {
        this.exitAction = exitAction;
        return this;
    }

    @Override
    public MenuGrid getGrid() {
        return this.grid;
    }

    @Override
    public MenuPane setGrid(final MenuGrid grid) {
        this.grid = grid;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o)
                || (o instanceof MenuPaneImpl other
                        && Objects.equals(this.title, other.title)
                        && Objects.equals(this.content, other.content)
                        && Objects.equals(this.exitAction, other.exitAction)
                        && Objects.equals(this.grid, other.grid));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.title, this.content, this.exitAction, this.grid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MenuPaneImpl.class.getSimpleName() + "{", "}")
                .add("title='" + this.title + "'")
                .add("content='" + this.content + "'")
                .add("exitAction=" + this.exitAction)
                .add("grid=" + this.grid)
                .toString();
    }
}
