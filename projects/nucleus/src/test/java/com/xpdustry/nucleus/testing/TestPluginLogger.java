// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.testing;

import com.xpdustry.foundation.plugin.PluginLogger;
import java.util.Arrays;

final class TestPluginLogger implements PluginLogger {

    @Override
    public void log(final Level level, final String message) {
        System.err.println("[" + level + "] " + message);
    }

    @Override
    public void log(final Level level, final String message, final Object... args) {
        System.err.println("[" + level + "] " + message + " " + Arrays.toString(args));
        if (args.length != 0 && args[args.length - 1] instanceof Throwable throwable) {
            throwable.printStackTrace(System.err);
        }
    }
}
