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
package com.xpdustry.catalina.antinsfw;

import com.xpdustry.distributor.api.annotation.PluginAnnotationProcessor;
import com.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginListener;
import org.jspecify.annotations.NonNull;

public final class CatalinaSecurityAntiNSFWPlugin extends AbstractMindustryPlugin {

    private final PluginAnnotationProcessor<?> processor = PluginAnnotationProcessor.events(this);

    @Override
    public void onInit() {
        this.addListener(new CanvasTracker(this));
    }

    @Override
    public void addListener(final @NonNull PluginListener listener) {
        super.addListener(listener);
        this.processor.process(listener);
    }
}
