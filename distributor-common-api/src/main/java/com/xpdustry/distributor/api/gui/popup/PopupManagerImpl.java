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

import arc.util.Align;
import arc.util.Interval;
import arc.util.Time;
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.gui.DisplayUnit;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.gui.transform.AbstractTransformerWindowManager;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.MindustryTimeUnit;
import java.time.Duration;
import mindustry.game.EventType;
import mindustry.gen.Call;

final class PopupManagerImpl extends AbstractTransformerWindowManager<PopupPane> implements PopupManager {

    private final Interval interval = new Interval();
    private Duration updateInterval = Duration.ZERO;
    private float tickUpdateInterval = 0F;
    private final EventSubscription updater;

    PopupManagerImpl(final MindustryPlugin plugin) {
        super(plugin);
        interval.reset(0, Float.MAX_VALUE);
        setUpdateInterval(Duration.ofSeconds(1L));
        updater = Distributor.get().getEventBus().subscribe(EventType.Trigger.update, plugin, () -> {
            if (interval.get(tickUpdateInterval)) {
                getWindows().values().forEach(Window::show);
            }
        });
    }

    @Override
    protected void onWindowOpen(final SimpleWindow window) {
        int align =
                switch (window.getPane().getAlignementX()) {
                    case LEFT -> Align.left;
                    case CENTER -> Align.center;
                    case RIGHT -> Align.right;
                };
        align |= switch (window.getPane().getAlignementY()) {
            case TOP -> Align.top;
            case CENTER -> Align.center;
            case BOTTOM -> Align.bottom;
        };
        Call.infoPopup(
                window.getViewer().con(),
                ComponentStringBuilder.mindustry(Distributor.get()
                                .getAudienceProvider()
                                .getPlayer(window.getViewer())
                                .getMetadata())
                        .append(window.getPane().getContent())
                        .toString(),
                (Time.delta / 60F) * tickUpdateInterval,
                align,
                window.getPane().getShiftY().asPixels(window.getViewer(), DisplayUnit.Axis.Y),
                window.getPane().getShiftX().asPixels(window.getViewer(), DisplayUnit.Axis.X),
                0,
                0);
    }

    @Override
    protected PopupPane createPane() {
        return PopupPane.create();
    }

    @Override
    protected void onDispose() {
        super.onDispose();
        updater.unsubscribe();
    }

    @Override
    public Duration getUpdateInterval() {
        return this.updateInterval;
    }

    @Override
    public void setUpdateInterval(final Duration updateInterval) {
        this.updateInterval = updateInterval;
        this.tickUpdateInterval =
                (float) MindustryTimeUnit.TICKS.convert(updateInterval.toMillis(), MindustryTimeUnit.MILLISECONDS);
    }
}
