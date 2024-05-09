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

final class PopupWindowImpl implements PopupWindow {

    private String content = "";
    private int shiftX = 0;
    private int shiftY = 0;
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
    public int getShiftX() {
        return this.shiftX;
    }

    @Override
    public void setShiftX(final int shiftX) {
        this.shiftX = shiftX;
    }

    @Override
    public int getShiftY() {
        return this.shiftY;
    }

    @Override
    public void setShiftY(final int shiftY) {
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
}
