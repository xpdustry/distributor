/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class DistributorProvider {

    private static @Nullable Distributor instance = null;

    private DistributorProvider() {}

    public static Distributor get() {
        if (DistributorProvider.instance == null) {
            throw new IllegalStateException("The API hasn't been initialized yet.");
        }
        return DistributorProvider.instance;
    }

    public static void set(final Distributor distributor) {
        if (DistributorProvider.instance != null) {
            throw new IllegalStateException("The API has already been initialized.");
        }
        DistributorProvider.instance = distributor;
    }
}
