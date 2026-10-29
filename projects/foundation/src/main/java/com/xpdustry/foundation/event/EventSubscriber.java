// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.event;

/// Handles a posted event.
///
/// @param <E> the event type
@FunctionalInterface
public interface EventSubscriber<E> {

    void onEvent(E event);
}
