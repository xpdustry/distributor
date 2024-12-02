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

import arc.util.Strings;
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.MutableKeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import mindustry.net.NetConnection;

public final class NetConnectionAudienceImpl extends BaseNetConnectionAudience {

    private final NetConnectionMetadata.Provider provider;

    public NetConnectionAudienceImpl(final NetConnection connection, final NetConnectionMetadata.Provider provider) {
        super(connection);
        this.provider = provider;
    }

    @Override
    public KeyContainer getMetadata() {
        final var container = MutableKeyContainer.create();
        final var metadata = this.provider.provide(this.connection);
        if (metadata != null) {
            if (metadata.muuid() != null) container.set(StandardKeys.MUUID, metadata.muuid());
            container.set(StandardKeys.COLOR, metadata.color());
            if (metadata.name() != null) {
                container.set(StandardKeys.NAME, Strings.stripColors(metadata.name()));
                container.set(
                        StandardKeys.DECORATED_NAME,
                        Distributor.get().getMindustryComponentDecoder().decode(metadata.name()));
            }
            if (metadata.locale() != null) container.set(StandardKeys.LOCALE, metadata.locale());
        }
        return KeyContainer.from(container);
    }
}
