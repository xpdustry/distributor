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
import com.xpdustry.distributor.api.component.NumberComponent;
import com.xpdustry.distributor.api.component.TemporalComponent;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.component.style.TemporalStyle;
import com.xpdustry.distributor.api.component.style.TextStyle;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

record MessageFormatTranslationImpl(String pattern, Locale locale) implements MessageFormatTranslation {

    private static final int MAX_NAMED_INDEX = 63;
    private static final Object[] EMPTY_ARRAY = new Object[MAX_NAMED_INDEX + 1];

    MessageFormatTranslationImpl {
        try {
            new MessageFormat(pattern, locale);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid pattern: " + pattern, e);
        }
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public String format(final TranslationArguments parameters) {
        return createFormat().format(getArguments(parameters).toArray());
    }

    @SuppressWarnings("JdkObsolete")
    @Override
    public void formatTo(final TranslationArguments parameters, final ComponentStringBuilder builder) {
        final var format = createFormat();
        final var arguments = getArguments(parameters);
        if (arguments.isEmpty()) {
            builder.append(format.format(null));
            return;
        }

        final var unformatted = format.format(EMPTY_ARRAY);
        final var iterator = format.formatToCharacterIterator(EMPTY_ARRAY);

        while (iterator.getIndex() < iterator.getEndIndex()) {
            final int end = iterator.getRunLimit();
            final var index = (Integer) iterator.getAttribute(MessageFormat.Field.ARGUMENT);
            if (index == null) {
                builder.append(unformatted, iterator.getIndex(), end);
            } else {
                var argument = arguments.get(index);
                var style = TextStyle.of();
                Component component = null;

                if (argument instanceof Component comp) {
                    style = comp.getTextStyle();
                    if (comp instanceof TemporalComponent temporal
                            && temporal.getTemporalStyle() instanceof TemporalStyle.None) {
                        argument = temporal.getTemporal();
                    } else if (comp instanceof NumberComponent number) {
                        argument = number.getNumber();
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

                builder.append(component);
            }
            iterator.setIndex(end);
        }
    }

    private List<Object> getArguments(final TranslationArguments arguments) {
        if (arguments instanceof TranslationArguments.Array array) {
            return array.getArguments();
        } else if (arguments instanceof TranslationArguments.Named named) {
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
            return entries;
        } else {
            throw new IllegalArgumentException("Unsupported arguments type: " + arguments.getClass());
        }
    }

    private MessageFormat createFormat() {
        return new MessageFormat(pattern, locale);
    }
}
