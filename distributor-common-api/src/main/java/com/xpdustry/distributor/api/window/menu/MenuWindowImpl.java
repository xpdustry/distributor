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

import com.xpdustry.distributor.api.window.Action;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MenuWindowImpl implements MenuWindow {

    private String title = "";
    private String content = "";
    private Action exitAction = Action.back();
    private final Grid grid = new GridImpl();

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
    public Grid getGrid() {
        return grid;
    }

    static final class GridImpl implements Grid {

        private final List<List<Option>> options = new ArrayList<>();

        @Override
        public List<List<Option>> getOptions() {
            return options.stream().map(List::copyOf).toList();
        }

        @Override
        public List<Option> getRow(int index) {
            return options.get(index);
        }

        @Override
        public void setRow(int index, List<Option> options) {
            this.options.set(index, new ArrayList<>(options));
        }

        @Override
        public void addRow(List<Option> options) {
            this.options.add(new ArrayList<>(options));
        }

        @Override
        public void addRow(int index, List<Option> options) {
            this.options.add(index, new ArrayList<>(options));
        }

        @Override
        public void removeRow(int index) {
            options.remove(index);
        }

        @Override
        public @Nullable Option getOption(final int id) {
            var i = 0;
            for (var row : options) {
                i += row.size();
                if (i > id) {
                    return row.get(id - i + row.size());
                }
            }
            return null;
        }

        @Override
        public Option getOption(final int x, final int y) {
            return options.get(y).get(x);
        }
    }
}
