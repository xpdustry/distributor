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

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.ValueComponent;
import com.xpdustry.distributor.api.component.render.ComponentAppendable;
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

record MessageFormatTranslation(String pattern, Locale locale) implements ComponentAwareTranslation {

    private static final int MAX_NAMED_INDEX = 63;
    private static final Object[] EMPTY_ARRAY = new Object[MAX_NAMED_INDEX + 1];

    MessageFormatTranslation {
        try {
            new MessageFormat(pattern, locale);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid pattern: " + pattern, e);
        }
    }

    @Override
    public void formatTo(final TranslationArguments parameters, final ComponentAppendable appendable) {
        if (parameters instanceof TranslationArguments.Empty) {
            formatTo(List.of(), appendable);
        } else if (parameters instanceof TranslationArguments.Array array) {
            formatTo(array.getArguments(), appendable);
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
            formatTo(entries, appendable);
        } else {
            throw new IllegalArgumentException("Unsupported arguments type: " + parameters.getClass());
        }
    }

    @SuppressWarnings("JdkObsolete")
    private void formatTo(final List<Object> arguments, final ComponentAppendable appendable) {
        final var format = new MessageFormat(pattern, locale);
        if (arguments.isEmpty()) {
            appendable.append(format.format(null));
        }

        final var buffer = format.format(EMPTY_ARRAY, new StringBuffer(), null);
        final var iterator = format.formatToCharacterIterator(EMPTY_ARRAY);

        while (iterator.getIndex() < iterator.getEndIndex()) {
            final int end = iterator.getRunLimit();
            final var index = (Integer) iterator.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index == null) {
                appendable.append(buffer, iterator.getIndex(), end);
            } else {
                var argument = arguments.get(index);
                var style = ComponentStyle.empty();
                Component component = null;

                if (argument instanceof ComponentLike like) {
                    var comp = like.asComponent();
                    if (comp instanceof ValueComponent<?> value) {
                        argument = value.getValue();
                        style = value.getStyle();
                    } else {
                        component = comp;
                    }
                }

                if (component == null) {
                    String result;
                    final var subformat = format.getFormatsByArgumentIndex()[index];
                    if (subformat == null) {
                        result = argument.toString();
                    } else {
                        try {
                            result = subformat
                                    .format(argument, new StringBuffer(), new FieldPosition(iterator.getIndex()))
                                    .toString();
                        } catch (final IllegalArgumentException e) {
                            result = argument.toString();
                        }
                    }
                    component = TextComponent.text(result, style);
                }

                appendable.append(component);
            }
            iterator.setIndex(end);
        }
    }
}
