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

import java.text.MessageFormat;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A helper class for adding localization support to your plugin.
 */
public interface LocalizationSource {

    /**
     * Returns a {@code LocalizationSource} for the router language :^).
     */
    static LocalizationSource router() {
        return RouterLocalizationSource.INSTANCE;
    }

    /**
     * Returns the localized string for the given key or {@code null} if absent.
     *
     * <pre> {@code
     *      // Send a localized message to every player
     *      final LocalizationSource source = ...;
     *      Groups.player.each(player -> {
     *          final var locale = Locale.forLanguageTag(player.locale().replace('_', '-'));
     *          final var message = source.localize("example.key", locale);
     *          player.sendMessage(message == null ? "???example.key???" : message);
     *      }
     * } </pre>
     *
     * @param key the key of the string to localize
     * @return the localized string contained in a {@link MessageFormat}, or {@code null} if no string was found.
     */
    @Nullable MessageFormat localize(final String key, final Locale locale);

    /**
     * Shorthand method to directly format a localized string, with a failover to a default value {@code ???key???}.
     *
     * <pre> {@code
     *      // Send a localized message to every player
     *      final LocalizationSource source = ...;
     *      Groups.player.each(player -> {
     *          final var locale = Locale.forLanguageTag(player.locale().replace('_', '-'));
     *          player.sendMessage(source.format("example.key", locale));
     *      }
     * } </pre>
     *
     * @param key    the key of the string to localize
     * @param locale the locale to use
     * @param args   the arguments to pass to the {@link MessageFormat#format(Object)}
     * @return the formatted string, or {@code ???key???} if no string was found.
     */
    default String format(final String key, final Locale locale, final Object... args) {
        final var format = this.localize(key, locale);
        return format == null ? "???" + key + "???" : format.format(args);
    }
}
