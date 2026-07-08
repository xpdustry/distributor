// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.util.Locale;
import org.jspecify.annotations.Nullable;

enum RouterTranslationSource implements TranslationSource {
    INSTANCE;

    static final Locale ROUTER_LOCALE = Locale.forLanguageTag("router");
    private static final Translation ROUTER_TRANSLATION = new TextTranslation("router");

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        return locale.getLanguage().equals(ROUTER_LOCALE.getLanguage()) ? ROUTER_TRANSLATION : null;
    }

    @Override
    public String toString() {
        return this.getDeclaringClass().getSimpleName();
    }
}
