// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.event;

/// Handles a posted event.
///
/// @param <E> the event type
@FunctionalInterface
public interface EventSubscriber<E> {

    void onEvent(final E event);
}
