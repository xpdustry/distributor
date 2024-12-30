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
package com.xpdustry.distributor.api.command;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Locale;
import org.immutables.value.Value;

/**
 * A general interface for dealing with command and command element descriptions.
 */
@DistributorDataClass
@Value.Immutable
public interface DescriptionFacade {

    DescriptionFacade EMPTY = DescriptionFacade.text("");

    /**
     * Creates a text description, which is just a simple string.
     *
     * @param text the text
     * @return the created text description
     */
    static DescriptionFacade text(final String text) {
        return DescriptionFacadeImpl.of(text);
    }

    /**
     * Creates a new translated description.
     * It uses distributor global translation system to translate the given key with either the input locale or the default locale.
     *
     * @param key           the translation key
     * @param defaultLocale the default locale
     * @return the created translated description
     */
    static DescriptionFacade translated(final String key, final Locale defaultLocale) {
        return new TranslatedDescriptionFacade(key, defaultLocale);
    }

    /**
     * Returns the description text.
     */
    String getText();

    /**
     * Returns the description text for the given command sender.
     *
     * @param sender the command sender
     * @return the description text
     */
    default String getText(final CommandSender sender) {
        return this.getText();
    }

    /**
     * Returns the description text as a {@link Component}.
     */
    default Component getComponent() {
        return Distributor.get().getMindustryComponentDecoder().decode(this.getText());
    }

    /**
     * Returns the description text as a {@link Component} for the given command sender.
     *
     * @param sender the command sender
     * @return the description {@link Component}
     */
    default Component getComponent(final CommandSender sender) {
        return Distributor.get().getMindustryComponentDecoder().decode(this.getText(sender));
    }

    /**
     * Returns whether this description is empty.
     */
    default boolean isEmpty() {
        return this.getText().isEmpty();
    }

    /**
     * Returns whether this description is empty for the given locale holder.
     *
     * @param sender the command sender
     * @return whether this description is empty
     */
    default boolean isEmpty(final CommandSender sender) {
        return this.getText(sender).isEmpty();
    }
}
