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
package com.xpdustry.catalina.common.gui;

import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.key.MutableKeyContainer;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

abstract class BaseTransformerWindow<P extends Pane, W extends BaseTransformerWindow<P, W>>
        implements TransformerWindow<P, W> {

    private final @Nullable MutableWindow<?> parent;
    private final PlayerAudience player;
    private final MutableKeyContainer state;
    private final List<Transformer<P>> transformers = new ArrayList<>();

    protected BaseTransformerWindow(final MutableWindow<?> parent) {
        this.parent = parent;
        this.player = parent.viewer();
        this.state = parent.state();
    }

    protected BaseTransformerWindow(final PlayerAudience player) {
        this.player = player;
    }

    @SuppressWarnings("unchecked")
    @Override
    public W addTransformer(final Transformer<P> transformer) {
        this.transformers.add(transformer);
        return (W) this;
    }

    protected abstract P createPane();

    protected final P createTransformedPane() {
        final var pane = createPane();
        for (var transformer : this.transformers) {
            transformer.transform(this, pane);
        }
        return pane;
    }
}
