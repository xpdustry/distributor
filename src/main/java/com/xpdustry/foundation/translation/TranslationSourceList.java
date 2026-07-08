// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import com.xpdustry.foundation.util.Priority;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/// A [TranslationSource] that queries child sources in priority order.
public final class TranslationSourceList implements TranslationSource {

    private final List<SourceWithPriority> pairs = new ArrayList<>();

    /// Adds a translation source with a priority.
    ///
    /// @param source the source to add
    /// @param priority the source priority
    public void add(final TranslationSource source, final Priority priority) {
        this.pairs.add(new SourceWithPriority(source, priority));
        this.pairs.sort(null);
    }

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        for (final var pair : this.pairs) {
            final var translation = pair.source.getTranslation(key, locale);
            if (translation != null) {
                return translation;
            }
        }
        return null;
    }

    private record SourceWithPriority(TranslationSource source, Priority priority)
            implements Comparable<SourceWithPriority> {

        @Override
        public int compareTo(final SourceWithPriority o) {
            return this.priority.compareTo(o.priority);
        }
    }
}
