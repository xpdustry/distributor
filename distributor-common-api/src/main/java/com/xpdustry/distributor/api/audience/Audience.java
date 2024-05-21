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
package com.xpdustry.distributor.api.audience;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.key.Key;
import com.xpdustry.distributor.api.key.TypedKey;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.permission.PermissionProvider;
import com.xpdustry.distributor.api.player.MUUID;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import mindustry.game.Team;

public interface Audience {

    TypedKey<String> NAME = TypedKey.of("name", Key.DISTRIBUTOR_NAMESPACE, String.class);

    TypedKey<String> DISPLAY_NAME = TypedKey.of("display-name", Key.DISTRIBUTOR_NAMESPACE, String.class);

    TypedKey<MUUID> MUUID = TypedKey.of("muuid", Key.DISTRIBUTOR_NAMESPACE, MUUID.class);

    TypedKey<Locale> LOCALE = TypedKey.of("locale", Key.DISTRIBUTOR_NAMESPACE, Locale.class);

    TypedKey<Team> TEAM = TypedKey.of("team", Key.DISTRIBUTOR_NAMESPACE, Team.class);

    static Audience of(final Audience... audiences) {
        if (audiences.length == 0) {
            return Audience.empty();
        } else if (audiences.length == 1) {
            return audiences[0];
        } else {
            return stream(() -> Stream.of(audiences).flatMap(Audience::getAudiences));
        }
    }

    static Audience of(final Iterable<Audience> audiences) {
        if (audiences instanceof Seq<Audience> seq) {
            return stream(() -> MindustryCollections.mutableList(seq).stream().flatMap(Audience::getAudiences));
        } else if (audiences instanceof ObjectSet<Audience> set) {
            return stream(() -> MindustryCollections.mutableSet(set).stream().flatMap(Audience::getAudiences));
        } else if (audiences instanceof Collection<Audience> collection) {
            return stream(() -> collection.stream().flatMap(Audience::getAudiences));
        } else {
            return stream(
                    () -> StreamSupport.stream(audiences.spliterator(), false).flatMap(Audience::getAudiences));
        }
    }

    static Audience empty() {
        return EmptyAudience.INSTANCE;
    }

    static Collector<Audience, ?, Audience> collectToAudience() {
        return Collectors.collectingAndThen(Collectors.toList(), Audience::of);
    }

    private static Audience stream(final StreamAudience audience) {
        return audience;
    }

    default void sendMessage(final String message) {}

    default void sendMessage(final Component component) {}

    default void sendMessage(final String message, final String unformatted, final Audience sender) {}

    default void sendMessage(final Component component, final Component unformatted, final Audience sender) {}

    default void sendWarning(final String message) {}

    default void sendWarning(final Component component) {}

    default void showHUDText(final String message) {}

    default void showHUDText(final Component component) {}

    default void hideHUDText() {}

    default void sendNotification(final String message, final char icon) {}

    default void sendNotification(final Component component, final char icon) {}

    default void sendAnnouncement(final String message) {}

    default void sendAnnouncement(final Component component) {}

    default void openURI(final URI uri) {}

    default void showLabel(final String label, final float x, final float y, final Duration duration) {}

    default void showLabel(final Component label, final float x, final float y, final Duration duration) {}

    default MetadataContainer getMetadata() {
        return MetadataContainer.empty();
    }

    default Stream<Audience> getAudiences() {
        return Stream.of(this);
    }

    default PermissionProvider getPermissions() {
        return PermissionProvider.empty();
    }
}
