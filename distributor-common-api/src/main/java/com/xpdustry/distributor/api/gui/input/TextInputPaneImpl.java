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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.BiAction;
import java.util.Objects;
import java.util.StringJoiner;

final class TextInputPaneImpl implements TextInputPane {

    private Component title = TextComponent.empty();
    private Component description = TextComponent.empty();
    private Component placeholder = TextComponent.empty();
    private int maxLength = 0;
    private BiAction<String> inputAction = BiAction.from(Action.back());
    private Action exitAction = Action.back();

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public TextInputPane setTitle(final Component title) {
        this.title = title;
        return this;
    }

    @Override
    public Component getDescription() {
        return this.description;
    }

    @Override
    public TextInputPane setDescription(final Component description) {
        this.description = description;
        return this;
    }

    @Override
    public Component getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public TextInputPane setPlaceholder(final Component placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public TextInputPane setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    @Override
    public BiAction<String> getInputAction() {
        return this.inputAction;
    }

    @Override
    public TextInputPane setInputAction(final BiAction<String> inputAction) {
        this.inputAction = inputAction;
        return this;
    }

    @Override
    public Action getExitAction() {
        return this.exitAction;
    }

    @Override
    public TextInputPane setExitAction(final Action exitAction) {
        this.exitAction = exitAction;
        return this;
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
