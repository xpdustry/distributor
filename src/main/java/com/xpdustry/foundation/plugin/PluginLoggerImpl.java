// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import arc.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

final class PluginLoggerImpl implements PluginLogger {

    private static final Object[] EMPTY_ARRAY = new Object[0];

    private final PluginMetadata metadata;

    public PluginLoggerImpl(final PluginMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void log(final Level level, final String message) {
        this.log(level, message, EMPTY_ARRAY);
    }

    @Override
    public void log(final Level level, final String message, Object... args) {
        final var prefix = "[" + this.metadata.displayName() + "] ";

        Throwable throwable = null;
        if (args.length != 0 && args[args.length - 1] instanceof Throwable t) {
            throwable = t;
            args = args.length == 1 ? EMPTY_ARRAY : Arrays.copyOf(args, args.length - 1);
        }

        final var levelForArc =
                switch (level) {
                    case ERROR -> Log.LogLevel.err;
                    case WARN -> Log.LogLevel.warn;
                    case INFO -> Log.LogLevel.info;
                    case DEBUG -> Log.LogLevel.debug;
                };

        Log.log(levelForArc, prefix + message, args);

        if (throwable != null) {
            final var sw = new StringWriter();
            final var pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            Log.log(levelForArc, prefix + sw);
        }
    }
}
