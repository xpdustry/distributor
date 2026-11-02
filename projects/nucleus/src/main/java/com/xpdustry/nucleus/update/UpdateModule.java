// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class UpdateModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        try {
            binder.bind(SignatureVerifier.class, SignatureVerifier.xpdustry());
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read the bundled xpdustry certificate", e);
        }
        binder.bind(DistributionInfoProvider.class, DistributionInfoProviderImpl.class);
        binder.bind(UpdateService.class);
        binder.bind(UpdateCommand.class);
        binder.bind(UpdateScheduler.class);
    }
}
