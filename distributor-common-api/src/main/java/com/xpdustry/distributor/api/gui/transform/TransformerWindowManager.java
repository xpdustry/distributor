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
package com.xpdustry.distributor.api.gui.transform;

import com.xpdustry.distributor.api.gui.Pane;
import com.xpdustry.distributor.api.gui.WindowManager;

/**
 * A window manager that uses transformers to create panes, used for rendering the GUI.
 *
 * @param <P> the type of the pane
 */
public interface TransformerWindowManager<P extends Pane> extends WindowManager {

    /**
     * Adds a transformer to this window manager.
     *
     * @param transformer the transformer
     */
    void addTransformer(final Transformer<P> transformer);
}
