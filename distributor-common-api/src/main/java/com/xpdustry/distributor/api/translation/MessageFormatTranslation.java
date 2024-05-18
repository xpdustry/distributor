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
package com.xpdustry.distributor.api.translation;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.component.ValueComponent;
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

record MessageFormatTranslation(String pattern, Locale locale) implements ComponentAwareTranslation {

    private static final int MAX_NAMED_INDEX = 63;

    MessageFormatTranslation {
        try {
            new MessageFormat(pattern, locale);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid pattern: " + pattern, e);
        }
    }

    @Override
    public String format(final TranslationArguments parameters, final Processor processor) {
        if (parameters instanceof TranslationArguments.Empty) {
            return format(List.of(), processor);
        } else if (parameters instanceof TranslationArguments.Array array) {
            return format(array.getArguments(), processor);
        } else if (parameters instanceof TranslationArguments.Named named) {
            final List<Object> entries = new ArrayList<>();
            for (final var entry : named.getArguments().entrySet()) {
                final int index;
                try {
                    index = Integer.parseInt(entry.getKey());
                } catch (NumberFormatException ignored) {
                    continue;
                }
                if (index > MAX_NAMED_INDEX) {
                    throw new IllegalArgumentException(
                            "Max argument index exceeded, expected less than " + MAX_NAMED_INDEX + ", got " + index);
                }
                for (int i = entries.size(); i <= index; i++) {
                    entries.add(null);
                }
                entries.set(index, entry.getValue());
            }
            return format(entries, processor);
        } else {
            throw new IllegalArgumentException("Unsupported arguments type: " + parameters.getClass());
        }
    }

    @SuppressWarnings("JdkObsolete")
    private String format(final List<Object> arguments, final Processor processor) {
        final var format = new MessageFormat(pattern, locale);
        if (arguments.isEmpty()) {
            return format.format(null);
        }

        final var builder = new StringBuilder();
        final var empty = new Object[arguments.size()];
        final var buffer = format.format(empty, new StringBuffer(), null);
        final var iterator = format.formatToCharacterIterator(empty);

        while (iterator.getIndex() < iterator.getEndIndex()) {
            final int end = iterator.getRunLimit();
            final var index = (Integer) iterator.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index != null) {
                var style = ComponentStyle.empty();
                var argument = arguments.get(index);
                if (argument instanceof ComponentLike like) {
                    final var component = like.asComponent();
                    style = component.getStyle();
                    if (component instanceof ValueComponent<?> value) {
                        argument = value.getValue();
                    } else {
                        argument = DistributorProvider.get()
                                .getPlainTextEncoder()
                                .encode(
                                        component,
                                        MetadataContainer.builder()
                                                .putConstant(Audience.LOCALE, locale)
                                                .build());
                    }
                }
                final var subformat = format.getFormatsByArgumentIndex()[index];
                if (subformat == null) {
                    builder.append(processor.process(argument.toString(), ComponentStyle.empty()));
                } else {
                    String result;
                    try {
                        result = subformat
                                .format(argument, new StringBuffer(), new FieldPosition(iterator.getIndex()))
                                .toString();
                    } catch (final IllegalArgumentException e) {
                        result = argument.toString();
                    }
                    builder.append(processor.process(result, style));
                }
            } else {
                builder.append(processor.process(buffer.substring(iterator.getIndex(), end), ComponentStyle.empty()));
            }
            iterator.setIndex(end);
        }

        return builder.toString();
    }
}
