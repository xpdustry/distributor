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

import arc.util.Align;
import arc.util.Interval;
import arc.util.Time;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.event.EventSubscription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.scheduler.MindustryTimeUnit;
import com.xpdustry.distributor.api.window.DisplayUnit;
import com.xpdustry.distributor.api.window.Window;
import com.xpdustry.distributor.api.window.transform.AbstractTransformerWindowFactory;
import java.time.Duration;
import mindustry.game.EventType;
import mindustry.gen.Call;

final class PopupWindowFactoryImpl extends AbstractTransformerWindowFactory<PopupWindow> implements PopupWindowFactory {

    private final Interval interval = new Interval();
    private Duration updateInterval = Duration.ZERO;
    private float tickUpdateInterval = 0F;
    private final EventSubscription updater;

    PopupWindowFactoryImpl(final MindustryPlugin plugin) {
        super(plugin);
        interval.reset(0, Float.MAX_VALUE);
        setUpdateInterval(Duration.ofSeconds(1L));
        updater = DistributorProvider.get().getEventBus().subscribe(EventType.Trigger.update, plugin, () -> {
            if (interval.get(tickUpdateInterval)) {
                getContexts().values().forEach(Window.Context::open);
            }
        });
    }

    @Override
    protected void onWindowOpen(final SimpleContext context) {
        int align =
                switch (context.getWindow().getAlignementX()) {
                    case LEFT -> Align.left;
                    case CENTER -> Align.center;
                    case RIGHT -> Align.right;
                };
        align |= switch (context.getWindow().getAlignementY()) {
            case TOP -> Align.top;
            case CENTER -> Align.center;
            case BOTTOM -> Align.bottom;
        };
        Call.infoPopup(
                context.getViewer().con(),
                context.getWindow().getContent(),
                (Time.delta / 60F) * tickUpdateInterval,
                align,
                context.getWindow().getShiftY().asPixels(context.getViewer(), DisplayUnit.Axis.Y),
                context.getWindow().getShiftX().asPixels(context.getViewer(), DisplayUnit.Axis.X),
                0,
                0);
    }

    @Override
    protected PopupWindow createWindow() {
        return new PopupWindowImpl();
    }

    @Override
    protected void onFactoryDispose() {
        super.onFactoryDispose();
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
