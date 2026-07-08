// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.event.EventPublisherImpl;
import com.xpdustry.foundation.plugin.BaseMindustryPlugin;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.foundation.scheduler.MindustrySchedulerImpl;
import com.xpdustry.foundation.scheduler.MindustryTimeSource;
import com.xpdustry.foundation.translation.TranslationSourceList;

/// Foundation's Mindustry plugin entrypoint.
public final class FoundationPlugin extends BaseMindustryPlugin implements FoundationAPI {

    private final TranslationSourceList translations = new TranslationSourceList();
    private final EventPublisher events = new EventPublisherImpl();
    private final MindustrySchedulerImpl scheduler =
            this.addListener(new MindustrySchedulerImpl(MindustryTimeSource.mindustry()));

    @Override
    public TranslationSourceList translations() {
        return this.translations;
    }

    @Override
    public EventPublisher events() {
        return this.events;
    }

    @Override
    public MindustryScheduler scheduler() {
        return this.scheduler;
    }
}
