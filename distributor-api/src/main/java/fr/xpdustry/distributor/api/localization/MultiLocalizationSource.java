/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.localization;

/**
 * A mutable localization source that delegates the localization lookup to other sources in FIFO order.
 */
public interface MultiLocalizationSource extends LocalizationSource {

    /**
     * Creates a new {@code MultiLocalizationSource} instance.
     */
    static MultiLocalizationSource create() {
        return new MultiLocalizationSourceImpl();
    }

    /**
     * Adds a localization source to the list of sources.
     *
     * @param source the source to add
     */
    void addLocalizationSource(final LocalizationSource source);
}
