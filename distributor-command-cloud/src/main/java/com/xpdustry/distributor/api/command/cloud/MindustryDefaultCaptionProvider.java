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

import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.DelegatingCaptionProvider;

/**
 * {@link Caption} instances for messages in distributor-command-cloud.
 */
public final class MindustryDefaultCaptionProvider<C> extends DelegatingCaptionProvider<C> {

    /**
     * Default caption for {@link MindustryCaptionKeys#ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND = "The player '<input>' does not exist";

    /**
     * Default caption for {@link MindustryCaptionKeys#ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY =
            "Too many players found for '<input>', be more specific";

    /**
     * Default caption for {@link MindustryCaptionKeys#ARGUMENT_PARSE_FAILURE_TEAM}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_TEAM = "Failed to find the team '<input>' with mode '<mode>'";

    /**
     * Default caption for {@link MindustryCaptionKeys#ARGUMENT_PARSE_FAILURE_CONTENT}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_CONTENT =
            "Cannot find any content named '<input>' of type '<type>'";

    private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
            .putCaption(
                    MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND,
                    ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND)
            .putCaption(
                    MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY, ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY)
            .putCaption(MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_TEAM, ARGUMENT_PARSE_FAILURE_TEAM)
            .putCaption(MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_CONTENT, ARGUMENT_PARSE_FAILURE_CONTENT)
            .build();

    @SuppressWarnings("unchecked")
    @Override
    public CaptionProvider<C> delegate() {
        return (CaptionProvider<C>) PROVIDER;
    }
}
