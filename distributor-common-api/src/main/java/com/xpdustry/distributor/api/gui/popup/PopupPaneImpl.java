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

import com.xpdustry.distributor.api.gui.DisplayUnit;
import java.util.Objects;
import java.util.StringJoiner;

final class PopupPaneImpl implements PopupPane {

    private String content = "";
    private DisplayUnit shiftX = DisplayUnit.Pixel.ZERO;
    private DisplayUnit shiftY = DisplayUnit.Pixel.ZERO;
    private AlignementX alignementX = AlignementX.CENTER;
    private AlignementY alignementY = AlignementY.CENTER;

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public DisplayUnit getShiftX() {
        return this.shiftX;
    }

    @Override
    public void setShiftX(final DisplayUnit shiftX) {
        this.shiftX = shiftX;
    }

    @Override
    public DisplayUnit getShiftY() {
        return this.shiftY;
    }

    @Override
    public void setShiftY(final DisplayUnit shiftY) {
        this.shiftY = shiftY;
    }

    @Override
    public AlignementX getAlignementX() {
        return this.alignementX;
    }

    @Override
    public void setAlignementX(final AlignementX alignementX) {
        this.alignementX = alignementX;
    }

    @Override
    public AlignementY getAlignementY() {
        return this.alignementY;
    }

    @Override
    public void setAlignementY(final AlignementY alignementY) {
        this.alignementY = alignementY;
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o)
                || (o instanceof PopupPaneImpl other
                        && this.content.equals(other.content)
                        && this.shiftX.equals(other.shiftX)
                        && this.shiftY.equals(other.shiftY)
                        && this.alignementX == other.alignementX
                        && this.alignementY == other.alignementY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.content, this.shiftX, this.shiftY, this.alignementX, this.alignementY);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PopupPaneImpl.class.getSimpleName() + "{", "}")
                .add("content='" + content + "'")
                .add("shiftX=" + shiftX)
                .add("shiftY=" + shiftY)
                .add("alignementX=" + alignementX)
                .add("alignementY=" + alignementY)
                .toString();
    }
}
