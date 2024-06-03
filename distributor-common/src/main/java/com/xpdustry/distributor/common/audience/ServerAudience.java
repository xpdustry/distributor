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
package com.xpdustry.distributor.common.audience;

import arc.util.Log;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import java.util.Locale;

final class ServerAudience implements Audience {

    static final Audience INSTANCE = new ServerAudience();

    private final MetadataContainer metadata = MetadataContainer.builder()
            .putConstant(StandardKeys.NAME, "server")
            .putConstant(StandardKeys.DISPLAY_NAME, "Server")
            .putSupplier(StandardKeys.LOCALE, Locale::getDefault)
            .build();

    private ServerAudience() {}

    @Override
    public void sendMessage(final String message) {
        Log.info(message);
    }

    @Override
    public void sendMessage(final ComponentLike component) {
        Log.info(render(component));
    }

    @Override
    public void sendMessage(final String message, final String unformatted, final Audience sender) {
        Log.info(message);
    }

    @Override
    public void sendMessage(final ComponentLike component, final ComponentLike unformatted, final Audience sender) {
        Log.info(render(component));
    }

    @Override
    public void sendWarning(final String message) {
        Log.warn(message);
    }

    @Override
    public void sendWarning(final ComponentLike component) {
        Log.warn(render(component));
    }

    @Override
    public MetadataContainer getMetadata() {
        return metadata;
    }

    private String render(final ComponentLike component) {
        return ComponentStringBuilder.ansi(getMetadata())
                .append(component.asComponent())
                .toString();
    }
}
