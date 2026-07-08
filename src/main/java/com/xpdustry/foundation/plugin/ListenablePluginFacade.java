// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import java.util.List;

public interface ListenablePluginFacade extends PluginFacade {

    /// Returns an unmodifiable view of the registered listeners
    List<PluginListener> listeners();

    /// Adds a lifecycle listener to this plugin.
    ///
    /// @param listener the listener to add
    /// @param <L> the listener type
    /// @return the added listener
    /// @throws IllegalStateException if the listener is already registered
    <L extends PluginListener> L addListener(final L listener);
}
