// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import org.jspecify.annotations.Nullable;

record TextTranslation(String text) implements Translation {

    @Override
    public String format() {
        return this.text;
    }

    @Override
    public String format(final @Nullable Object... args) {
        return this.text;
    }
}
