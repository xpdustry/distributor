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
import com.xpdustry.distributor.api.component.ComponentLike;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An audience that forwards all operations to a collection of sub audiences.
 */
@FunctionalInterface
public interface ForwardingAudience extends Audience {

    @Override
    Iterable<Audience> getAudiences();

    @Override
    default Stream<Audience> toStream() {
        final var audiences = getAudiences();
        final Stream<Audience> stream;
        if (audiences instanceof Collection<Audience> collection) {
            stream = collection.stream();
        } else if (audiences instanceof Seq<Audience> seq) {
            stream = MindustryCollections.mutableList(seq).stream();
        } else if (audiences instanceof ObjectSet<Audience> set) {
            stream = MindustryCollections.mutableSet(set).stream();
        } else {
            stream = StreamSupport.stream(audiences.spliterator(), false);
        }
        return stream.flatMap(Audience::toStream);
    }

    @Override
    default void sendMessage(final ComponentLike component) {
        for (final var audience : getAudiences()) {
            audience.sendMessage(component);
        }
    }

    @Override
    default void sendMessage(final ComponentLike component, final ComponentLike unformatted, final Audience sender) {
        for (final var audience : getAudiences()) {
            audience.sendMessage(component, unformatted, sender);
        }
    }

    @Override
    default void sendWarning(final ComponentLike component) {
        for (final var audience : getAudiences()) {
            audience.sendWarning(component);
        }
    }

    @Override
    default void showHUDText(final ComponentLike component) {
        for (final var audience : getAudiences()) {
            audience.showHUDText(component);
        }
    }

    @Override
    default void hideHUDText() {
        for (final var audience : getAudiences()) {
            audience.hideHUDText();
        }
    }

    @Override
    default void sendNotification(final ComponentLike component, final char icon) {
        for (final var audience : getAudiences()) {
            audience.sendNotification(component, icon);
        }
    }

    @Override
    default void sendAnnouncement(final ComponentLike component) {
        for (final var audience : getAudiences()) {
            audience.sendAnnouncement(component);
        }
    }

    @Override
    default void openURI(final URI uri) {
        for (final var audience : getAudiences()) {
            audience.openURI(uri);
        }
    }

    @Override
    default void showLabel(final ComponentLike label, final float x, final float y, final Duration duration) {
        for (final var audience : getAudiences()) {
            audience.showLabel(label, x, y, duration);
        }
    }

    @Override
    default void kick(final ComponentLike reason, final Duration duration) {
        for (final var audience : getAudiences()) {
            audience.kick(reason, duration);
        }
    }
}
