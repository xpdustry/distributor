// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.text.MessageFormat;
import org.jspecify.annotations.Nullable;

record MessageFormatTranslation(MessageFormat mf) implements Translation {

    @Override
    public String format() {
        return this.mf.format(null);
    }

    @Override
    public String format(final @Nullable Object... args) {
        return this.mf.format(args);
    }
}
