// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.gatekeeper;

import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.dependency.Inject;
import com.xpdustry.nucleus.pipeline.AbstractProcessorPipeline;

public final class GatekeeperPipeline extends AbstractProcessorPipeline<GatekeeperContext, GatekeeperDecision> {

    @Inject
    public GatekeeperPipeline(final PluginLogger logger) {
        super("gatekeeper", logger);
    }

    @Override
    public GatekeeperDecision pump(final GatekeeperContext context) {
        for (final var processor : this.processors()) {
            final GatekeeperDecision decision;
            try {
                decision = processor.process(context);
            } catch (final Exception e) {
                this.logger().error("Error while verifying player {}", context.name(), e);
                continue;
            }
            if (decision instanceof GatekeeperDecision.Kick) {
                return decision;
            }
        }
        return GatekeeperDecision.ALLOW;
    }
}
