// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class MessageModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(Messenger.class);
        binder.bind(ServerInfoProvider.class, MindustryServerInfoProvider.class);
    }
}
