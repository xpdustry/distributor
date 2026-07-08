// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.text.MessageFormat;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/// Represents a translation from a translation system.
public interface Translation {

    /// Creates a translation that always formats to the given text.
    ///
    /// @param text the text to return
    /// @return the created translation
    static Translation ofText(final String text) {
        return new TextTranslation(text);
    }

    /// Creates a translation backed by [MessageFormat].
    ///
    /// @param pattern the message format pattern
    /// @param locale the locale of the message format
    /// @return the created translation
    static Translation ofMessageFormat(final String pattern, final Locale locale) {
        return new MessageFormatTranslation(new MessageFormat(pattern, locale));
    }

    /// Formats the translation without arguments.
    ///
    /// @return the formatted translation
    String format();

    /// Formats the translation with the given arguments.
    ///
    /// @param args the translation arguments
    /// @return the formatted translation
    String format(final @Nullable Object... args);
}
