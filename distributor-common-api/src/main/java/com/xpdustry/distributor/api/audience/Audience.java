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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mindustry.net.Packets;

/**
 * A universal interface for sending Mindustry content to receivers.
 */
public interface Audience {

    /**
     * Creates an audience that forwards its content to the given audiences.
     *
     * @param audiences the audiences
     * @return the forwarding audience
     */
    static Audience of(final Audience... audiences) {
        if (audiences.length == 0) {
            return Audience.empty();
        } else if (audiences.length == 1) {
            return audiences[0];
        } else {
            return of(List.of(audiences));
        }
    }

    /**
     * Creates an audience that forwards its content to the given audiences.
     *
     * @param audiences the audiences
     * @return the forwarding audience
     */
    static Audience of(final Iterable<Audience> audiences) {
        return ((ForwardingAudience) () -> audiences);
    }

    /**
     * Returns an empty audience that does nothing.
     */
    static Audience empty() {
        return EmptyAudience.INSTANCE;
    }

    /**
     * Returns a collector, combining a stream of audiences into a single forwarding audience.
     */
    static Collector<Audience, ?, Audience> collectToAudience() {
        return Collectors.collectingAndThen(Collectors.toList(), Audience::of);
    }

    /**
     * Sends a message to this audience.
     *
     * @param component the message
     */
    default void sendMessage(final Component component) {}

    /**
     * Sends a message to this audience.
     *
     * @param component the message
     * @param unformatted the unformatted message
     * @param sender the sender
     */
    default void sendMessage(final Component component, final Component unformatted, final Audience sender) {}

    /**
     * Sends a warning to this audience.
     *
     * @param component the warning
     */
    default void sendWarning(final Component component) {}

    /**
     * Shows a HUD text to this audience.
     *
     * @param component the HUD text
     */
    default void showHUDText(final Component component) {}

    /**
     * Hides the HUD text of this audience.
     */
    default void hideHUDText() {}

    /**
     * Sends a notification to this audience.
     *
     * @param component the notification
     * @param icon the icon
     */
    default void sendNotification(final Component component, final char icon) {}

    /**
     * Sends an announcement to this audience.
     *
     * @param component the announcement
     */
    default void sendAnnouncement(final Component component) {}

    /**
     * Sends an uri to open to this audience.
     *
     * @param uri the uri
     */
    default void openURI(final URI uri) {}

    /**
     * Shows a label to this audience.
     *
     * @param label the label
     * @param x the x position in world coordinates
     * @param y the y position in world coordinates
     * @param duration the duration
     */
    default void showLabel(final Component label, final float x, final float y, final Duration duration) {}

    /**
     * Kicks this audience from the server.
     *
     * @param reason the reason
     * @param duration the duration
     */
    default void kick(final Component reason, final Duration duration) {}

    /**
     * Kicks this audience from the server.
     *
     * @param reason the reason
     * @param duration the duration
     */
    default void kick(final Packets.KickReason reason, final Duration duration) {}

    /**
     * Returns the metadata of this audience.
     */
    default KeyContainer getMetadata() {
        return KeyContainer.empty();
    }

    /**
     * Returns the audiences this audience forwards to.
     */
    default Iterable<Audience> getAudiences() {
        return List.of(this);
    }

    /**
     * Returns the audiences this audience forwards to as a stream.
     */
    default Stream<Audience> toStream() {
        return Stream.of(this);
    }

    /**
     * Returns the permissions of this audience.
     */
    default PermissionContainer getPermissions() {
        return PermissionContainer.empty();
    }
}
