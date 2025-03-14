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
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import mindustry.net.Packets;

/**
 * An audience that forwards all operations to a collection of sub audiences.
 */
@FunctionalInterface
public interface ForwardingAudience extends Audience {

    @Override
    Iterable<Audience> getAudiences();

    @Override
    default Stream<Audience> toStream() {
        final var audiences = this.getAudiences();
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
    default void sendMessage(final Component component) {
        for (final var audience : this.getAudiences()) {
            audience.sendMessage(component);
        }
    }

    @Override
    default void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        for (final var audience : this.getAudiences()) {
            audience.sendMessage(component, unformatted, sender);
        }
    }

    @Override
    default void sendWarning(final Component component) {
        for (final var audience : this.getAudiences()) {
            audience.sendWarning(component);
        }
    }

    @Override
    default void showHUDText(final Component component) {
        for (final var audience : this.getAudiences()) {
            audience.showHUDText(component);
        }
    }

    @Override
    default void hideHUDText() {
        for (final var audience : this.getAudiences()) {
            audience.hideHUDText();
        }
    }

    @Override
    default void sendNotification(final Component component, final char icon) {
        for (final var audience : this.getAudiences()) {
            audience.sendNotification(component, icon);
        }
    }

    @Override
    default void sendAnnouncement(final Component component) {
        for (final var audience : this.getAudiences()) {
            audience.sendAnnouncement(component);
        }
    }

    @Override
    default void openURI(final URI uri) {
        for (final var audience : this.getAudiences()) {
            audience.openURI(uri);
        }
    }

    @Override
    default void showLabel(final Component label, final float x, final float y, final Duration duration) {
        for (final var audience : this.getAudiences()) {
            audience.showLabel(label, x, y, duration);
        }
    }

    @Override
    default void kick(final Component reason, final Duration duration, final boolean log) {
        for (final var audience : this.getAudiences()) {
            audience.kick(reason, duration, log);
        }
    }

    @Override
    default void kick(final Packets.KickReason reason, final Duration duration, final boolean log) {
        for (final var audience : this.getAudiences()) {
            audience.kick(reason, duration, log);
        }
    }
}
