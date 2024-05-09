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
import com.xpdustry.distributor.api.window.Window;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

public interface MenuWindow extends Window {

    static MenuWindow create() {
        return new MenuWindowImpl();
    }

    String getTitle();

    void setTitle(final String title);

    String getContent();

    void setContent(final String content);

    Action getExitAction();

    void setExitAction(final Action action);

    Grid getGrid();

    interface Grid {

        List<List<Option>> getOptions();

        List<Option> getRow(final int index);

        void setRow(final int index, final List<Option> options);

        default void addRow(final Option... options) {
            addRow(Arrays.asList(options));
        }

        void addRow(final List<Option> options);

        default void addRow(final int index, final Option... options) {
            addRow(index, Arrays.asList(options));
        }

        void addRow(final int index, final List<Option> options);

        void removeRow(final int index);

        @Nullable Option getOption(final int id);

        Option getOption(final int x, final int y);
    }

    @DistributorDataClass
    @Value.Immutable
    interface Option {

        static Option of(final String content, final Action action) {
            return OptionImpl.of(content, action);
        }

        String getContent();

        Action getAction();
    }
}
