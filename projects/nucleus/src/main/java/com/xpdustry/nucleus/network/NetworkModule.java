// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.network;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class NetworkModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(InetAddressProvider.class);
        binder.bind(InetAddressInfoProvider.class);
        binder.bind(InetAddressWhitelist.class);
    }
}
