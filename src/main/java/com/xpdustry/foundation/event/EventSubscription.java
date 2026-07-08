// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.event;

/// A subscription to an event.
public interface EventSubscription {

    /// Unsubscribes the bound subscriber from the event.
    void unsubscribe();
}
