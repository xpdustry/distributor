/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.logging.simple;

import arc.util.Log;
import mindustry.mod.Plugin;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public final class DistributorLoggerPlugin extends Plugin {

    static {
        initialize();
    }

    private static void initialize() {
        // Class loader trickery to use the ModClassLoader instead of the root
        final var temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DistributorLoggerPlugin.class.getClassLoader());
        try {
            if (!(LoggerFactory.getILoggerFactory() instanceof DistributorLoggerFactory)) {
                Log.err(
                        """
                        The slf4j Logger factory isn't provided by Distributor (got @ instead of DistributorLoggerFactory).
                        Make sure another plugin doesn't set it's own logging implementation or that it's logging implementation is relocated correctly.
                        """,
                        LoggerFactory.getILoggerFactory().getClass().getName());
                return;
            }

            // Redirect JUL to SLF4J
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();

            LoggerFactory.getLogger(DistributorLoggerPlugin.class).info("Initialized simple logger");
        } finally {
            // Restore the class loader
            Thread.currentThread().setContextClassLoader(temp);
        }
    }
}
