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

import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.key.Key;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.permission.PermissionProvider;
import com.xpdustry.distributor.api.player.MUUID;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mindustry.game.Team;

public interface Audience {

    Key<String> NAME = Key.of(Key.DISTRIBUTOR_NAMESPACE, "name", String.class);

    Key<String> DISPLAY_NAME = Key.of(Key.DISTRIBUTOR_NAMESPACE, "display-name", String.class);

    Key<MUUID> MUUID = Key.of(Key.DISTRIBUTOR_NAMESPACE, "muuid", MUUID.class);

    Key<Locale> LOCALE = Key.of(Key.DISTRIBUTOR_NAMESPACE, "locale", Locale.class);

    Key<Team> TEAM = Key.of(Key.DISTRIBUTOR_NAMESPACE, "team", Team.class);

    static Audience of(final Audience... audiences) {
        if (audiences.length == 0) {
            return Audience.empty();
        } else if (audiences.length == 1) {
            return audiences[0];
        } else {
            return of(List.of(audiences));
        }
    }

    static Audience of(final Iterable<Audience> audiences) {
        return ((ForwardingAudience) () -> audiences);
    }

    static Audience empty() {
        return EmptyAudience.INSTANCE;
    }

    static Collector<Audience, ?, Audience> collectToAudience() {
        return Collectors.collectingAndThen(Collectors.toList(), Audience::of);
    }

    default void sendMessage(final String message) {}

    default void sendMessage(final ComponentLike component) {}

    default void sendMessage(final String message, final String unformatted, final Audience sender) {}

    default void sendMessage(final ComponentLike component, final ComponentLike unformatted, final Audience sender) {}

    default void sendWarning(final String message) {}

    default void sendWarning(final ComponentLike component) {}

    default void showHUDText(final String message) {}

    default void showHUDText(final ComponentLike component) {}

    default void hideHUDText() {}

    default void sendNotification(final String message, final char icon) {}

    default void sendNotification(final ComponentLike component, final char icon) {}

    default void sendAnnouncement(final String message) {}

    default void sendAnnouncement(final ComponentLike component) {}

    default void openURI(final URI uri) {}

    default void showLabel(final String label, final float x, final float y, final Duration duration) {}

    default void showLabel(final ComponentLike label, final float x, final float y, final Duration duration) {}

    default MetadataContainer getMetadata() {
        return MetadataContainer.empty();
    }

    default Iterable<Audience> getAudiences() {
        return List.of(this);
    }

    default Stream<Audience> asStream() {
        return Stream.of(this);
    }

    default PermissionProvider getPermissions() {
        return PermissionProvider.empty();
    }
}
