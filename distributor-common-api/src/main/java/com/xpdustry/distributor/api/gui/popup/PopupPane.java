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
package com.xpdustry.distributor.api.gui.popup;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.gui.DisplayUnit;
import com.xpdustry.distributor.api.gui.Pane;

/**
 * A popup pane.
 */
public interface PopupPane extends Pane {

    static PopupPane create() {
        return new PopupPaneImpl();
    }

    Component getContent();

    PopupPane setContent(final Component content);

    default PopupPane setContent(final String content) {
        return this.setContent(Distributor.get().getMindustryComponentDecoder().decode(content));
    }

    DisplayUnit getShiftX();

    PopupPane setShiftX(final DisplayUnit shiftX);

    DisplayUnit getShiftY();

    PopupPane setShiftY(final DisplayUnit shiftY);

    AlignementX getAlignementX();

    PopupPane setAlignementX(final AlignementX alignementX);

    AlignementY getAlignementY();

    PopupPane setAlignementY(final AlignementY alignementY);

    enum AlignementX {
        LEFT,
        CENTER,
        RIGHT,
    }

    enum AlignementY {
        TOP,
        CENTER,
        BOTTOM,
    }
}
