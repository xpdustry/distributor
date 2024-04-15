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
package com.xpdustry.distributor.api.command.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.StandardCaptionKeys;

/**
 * {@link Caption} instances for {@link MindustryCommandManager} error messages.
 *
 * @see StandardCaptionKeys
 */
public final class MindustryCaptionKeys {

    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>(3);

    /**
     * Variables: {@code {input}}.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND = of("argument.parse.failure.player.not_found");

    /**
     * Variables: {@code {input}}.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY = of("argument.parse.failure.player.too_many");

    /**
     * Variables: {@code {input}}, {@code {teamMode}}.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_TEAM = of("argument.parse.failure.team");

    /**
     * Variables: {@code {input}}, {@code {type}}.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_CONTENT = of("argument.parse.failure.content");

    private MindustryCaptionKeys() {}

    private static Caption of(final String key) {
        final var caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    /**
     * Returns an unmodifiable view of all the captions used in the {@link MindustryCommandManager}.
     */
    public static Collection<Caption> getCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}
