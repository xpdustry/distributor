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

import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.BiAction;
import com.xpdustry.distributor.api.gui.Pane;

public interface TextInputPane extends Pane {

    static TextInputPane create() {
        return new TextInputPaneImpl();
    }

    String getTitle();

    void setTitle(final String title);

    String getDescription();

    void setDescription(final String description);

    String getPlaceholder();

    void setPlaceholder(final String placeholder);

    int getMaxLength();

    void setMaxLength(final int maxLength);

    BiAction<String> getInputAction();

    void setInputAction(final BiAction<String> inputAction);

    Action getExitAction();

    void setExitAction(final Action exitAction);
}
