// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.util.Optional;

@FunctionalInterface
public interface DistributionInfoProvider {

    /// @return empty if the server cannot be updated, such as when it does not run from jars
    Optional<DistributionInfo> get();
}
