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
package com.xpdustry.distributor.api.component.render;

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.key.KeyContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jspecify.annotations.Nullable;

import static com.xpdustry.distributor.api.component.TextComponent.text;

/**
 * A special {@link ComponentStringBuilder} that does not build a string with its appended elements.
 * Instead, they are accumulated in a freely accessible list.
 */
public final class ComponentAccumulator implements ComponentStringBuilder {

    /**
     * Creates a new {@link ComponentAccumulator} instance.
     */
    public static ComponentAccumulator create() {
        return new ComponentAccumulator();
    }

    private final List<Component> components = new ArrayList<>();

    ComponentAccumulator() {}

    /**
     * Returns an immutable view of the accumulated components.
     */
    public List<Component> getComponents() {
        return Collections.unmodifiableList(this.components);
    }

    @Override
    public KeyContainer getContext() {
        return KeyContainer.empty();
    }

    @Override
    public ComponentAccumulator append(final Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public ComponentAccumulator append(final @Nullable CharSequence csq) {
        return this.append(text(csq == null ? "null" : csq.toString()));
    }

    @Override
    public ComponentAccumulator append(final @Nullable CharSequence csq, final int start, final int end) {
        return this.append(
                text(csq == null ? "null" : csq.subSequence(start, end).toString()));
    }

    @Override
    public ComponentAccumulator append(final char c) {
        return this.append(text(c));
    }

    @Override
    public String toString() {
        return "CompCompStringBuilder{" + "components=" + this.components + '}';
    }
}
