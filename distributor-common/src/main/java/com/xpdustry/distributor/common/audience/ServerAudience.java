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
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.key.DynamicKeyContainer;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import java.util.Locale;

public enum ServerAudience implements Audience {
    INSTANCE;

    @SuppressWarnings("ImmutableEnumChecker")
    private final KeyContainer metadata = DynamicKeyContainer.builder()
            .putConstant(StandardKeys.NAME, "server")
            .putConstant(StandardKeys.DECORATED_NAME, TextComponent.text("Server"))
            .putSupplied(StandardKeys.LOCALE, Locale::getDefault)
            .build();

    @Override
    public void sendMessage(final Component component) {
        Log.info(render(component));
    }

    @Override
    public void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        Log.info(render(component));
    }

    @Override
    public void sendWarning(final Component component) {
        Log.warn(render(component));
    }

    @Override
    public KeyContainer getMetadata() {
        return metadata;
    }

    @Override
    public PermissionContainer getPermissions() {
        return PermissionContainer.all();
    }

    private String render(final Component component) {
        return ComponentStringBuilder.ansi(getMetadata()).append(component).toString();
    }
}
