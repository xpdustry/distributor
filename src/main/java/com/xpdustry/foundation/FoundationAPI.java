// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.foundation.translation.TranslationSourceList;
import java.util.Objects;
import mindustry.Vars;

/// The foundation-common API.
public interface FoundationAPI extends PluginListener {

    /// Returns the global [FoundationAPI] instance.
    static FoundationAPI get() {
        return (FoundationAPI)
                Objects.requireNonNull(Vars.mods.getMod(FoundationPlugin.class), "foundation is not loaded").main;
    }

    /// Returns the global translation source list.
    TranslationSourceList translations();

    /// Returns the event publisher.
    EventPublisher events();

    /// Returns the plugin scheduler.
    MindustryScheduler scheduler();
}
