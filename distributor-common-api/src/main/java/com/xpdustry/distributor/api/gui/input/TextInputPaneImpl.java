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
import java.util.Objects;
import java.util.StringJoiner;

final class TextInputPaneImpl implements TextInputPane {

    private String title = "";
    private String description = "";
    private String placeholder = "";
    private int maxLength = 0;
    private BiAction<String> inputAction = BiAction.from(Action.back());
    private Action exitAction = Action.back();

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public BiAction<String> getInputAction() {
        return this.inputAction;
    }

    @Override
    public void setInputAction(final BiAction<String> inputAction) {
        this.inputAction = inputAction;
    }

    @Override
    public Action getExitAction() {
        return this.exitAction;
    }

    @Override
    public void setExitAction(final Action exitAction) {
        this.exitAction = exitAction;
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o)
                || (o instanceof TextInputPaneImpl other
                        && maxLength == other.maxLength
                        && Objects.equals(title, other.title)
                        && Objects.equals(description, other.description)
                        && Objects.equals(placeholder, other.placeholder)
                        && Objects.equals(inputAction, other.inputAction)
                        && Objects.equals(exitAction, other.exitAction));
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, placeholder, maxLength, inputAction, exitAction);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TextInputPaneImpl.class.getSimpleName() + "{", "}")
                .add("title='" + title + "'")
                .add("description='" + description + "'")
                .add("placeholder='" + placeholder + "'")
                .add("maxLength=" + maxLength)
                .add("inputAction=" + inputAction)
                .add("exitAction=" + exitAction)
                .toString();
    }
}
