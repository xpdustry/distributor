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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.TextComponent;
import java.util.Objects;
import java.util.StringJoiner;

final class MenuPaneImpl implements MenuPane {

    private Component title = TextComponent.empty();
    private Component content = TextComponent.empty();
    private Action exit = Action.back();
    private MenuGrid grid = MenuGrid.create();

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public MenuPane title(final Component title) {
        this.title = title;
        return this;
    }

    @Override
    public Component content() {
        return this.content;
    }

    @Override
    public MenuPane content(final Component content) {
        this.content = content;
        return this;
    }

    @Override
    public Action exit() {
        return this.exit;
    }

    @Override
    public MenuPane exit(final Action exitAction) {
        this.exit = exitAction;
        return this;
    }

    @Override
    public MenuGrid grid() {
        return this.grid;
    }

    @Override
    public MenuPane grid(final MenuGrid grid) {
        this.grid = grid;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o)
                || (o instanceof MenuPaneImpl other
                        && Objects.equals(this.title, other.title)
                        && Objects.equals(this.content, other.content)
                        && Objects.equals(this.exit, other.exit)
                        && Objects.equals(this.grid, other.grid));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.title, this.content, this.exit, this.grid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MenuPaneImpl.class.getSimpleName() + "{", "}")
                .add("title='" + this.title + "'")
                .add("content='" + this.content + "'")
                .add("exitAction=" + this.exit)
                .add("grid=" + this.grid)
                .toString();
    }
}
