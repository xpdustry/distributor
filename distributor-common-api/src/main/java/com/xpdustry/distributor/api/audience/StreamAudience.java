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
import java.net.URI;
import java.util.stream.Stream;

@FunctionalInterface
public interface StreamAudience extends Audience {

    @Override
    Stream<Audience> getAudiences();

    @Override
    default void sendMessage(final String message) {
        getAudiences().forEach(audience -> audience.sendMessage(message));
    }

    @Override
    default void sendMessage(final Component component) {
        getAudiences().forEach(audience -> audience.sendMessage(component));
    }

    @Override
    default void sendMessage(final String message, final String unformatted, final Audience sender) {
        getAudiences().forEach(audience -> audience.sendMessage(message, unformatted, sender));
    }

    @Override
    default void sendMessage(final Component component, final Component unformatted, final Audience sender) {
        getAudiences().forEach(audience -> audience.sendMessage(component, unformatted, sender));
    }

    @Override
    default void sendWarning(final String message) {
        getAudiences().forEach(audience -> audience.sendWarning(message));
    }

    @Override
    default void sendWarning(final Component component) {
        getAudiences().forEach(audience -> audience.sendWarning(component));
    }

    @Override
    default void showHUDText(final String message) {
        getAudiences().forEach(audience -> audience.showHUDText(message));
    }

    @Override
    default void showHUDText(final Component component) {
        getAudiences().forEach(audience -> audience.showHUDText(component));
    }

    @Override
    default void hideHUDText() {
        getAudiences().forEach(Audience::hideHUDText);
    }

    @Override
    default void sendNotification(final String message, final char icon) {
        getAudiences().forEach(audience -> audience.sendNotification(message, icon));
    }

    @Override
    default void sendNotification(final Component component, final char icon) {
        getAudiences().forEach(audience -> audience.sendNotification(component, icon));
    }

    @Override
    default void sendAnnouncement(final String message) {
        getAudiences().forEach(audience -> audience.sendAnnouncement(message));
    }

    @Override
    default void sendAnnouncement(final Component component) {
        getAudiences().forEach(audience -> audience.sendAnnouncement(component));
    }

    @Override
    default void openURI(final URI uri) {
        getAudiences().forEach(audience -> audience.openURI(uri));
    }
}
