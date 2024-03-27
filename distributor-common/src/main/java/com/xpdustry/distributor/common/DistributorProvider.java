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
package com.xpdustry.distributor.common;

import org.jspecify.annotations.Nullable;

/**
 * A holder for the global {@link Distributor} factory.
 */
public final class DistributorProvider {

    private static @Nullable Distributor instance = null;

    private DistributorProvider() {}

    /**
     * Returns the global {@link Distributor} factory.
     * @throws DistributorInitializationException if the API hasn't been initialized yet
     */
    public static Distributor get() {
        if (DistributorProvider.instance == null) {
            throw new DistributorInitializationException("The API hasn't been initialized yet.");
        }
        return DistributorProvider.instance;
    }

    /**
     * Sets the global {@link Distributor} factory.
     * @throws DistributorInitializationException if the API has already been initialized
     */
    public static void set(final Distributor distributor) {
        if (DistributorProvider.instance != null) {
            throw new DistributorInitializationException("The API has already been initialized.");
        }
        DistributorProvider.instance = distributor;
    }

    /**
     * Clears the global {@link Distributor} factory.
     * @throws DistributorInitializationException if the API hasn't been initialized yet
     */
    public static void clear() {
        if (DistributorProvider.instance != null) {
            DistributorProvider.instance = null;
        } else {
            throw new DistributorInitializationException("The API hasn't been initialized yet.");
        }
    }

    /**
     * Returns whether the API has been initialized.
     */
    public static boolean isInitialized() {
        return DistributorProvider.instance != null;
    }
}
