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
package com.xpdustry.distributor.api.window.popup;

import com.xpdustry.distributor.api.window.DisplayUnit;
import com.xpdustry.distributor.api.window.Window;

public interface PopupWindow extends Window {

    static PopupWindow create() {
        return new PopupWindowImpl();
    }

    String getContent();

    void setContent(final String content);

    DisplayUnit getShiftX();

    void setShiftX(final DisplayUnit shiftX);

    DisplayUnit getShiftY();

    void setShiftY(final DisplayUnit shiftY);

    AlignementX getAlignementX();

    void setAlignementX(final AlignementX alignementX);

    AlignementY getAlignementY();

    void setAlignementY(final AlignementY alignementY);

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
