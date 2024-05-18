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
package com.xpdustry.distributor.component.codec;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.NumberComponent;
import com.xpdustry.distributor.api.component.TemporalComponent;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.TranslatableComponent;
import com.xpdustry.distributor.api.component.codec.StringComponentEncoder;
import com.xpdustry.distributor.api.component.style.ComponentStyle;
import com.xpdustry.distributor.api.metadata.MetadataContainer;
import com.xpdustry.distributor.api.translation.ComponentAwareTranslation;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractStringComponentEncoder implements StringComponentEncoder {

    @Override
    public String encode(final Component input, final MetadataContainer context) {
        final var appender = createAppender(null);
        render(appender, input, context);
        return appender.toString();
    }

    private void render(final Appender appender, final Component input, final MetadataContainer context) {
        appender.appendStyleStart(input.getStyle());

        if (input instanceof TextComponent text) {
            appender.appendText(text.getContent());
        } else if (input instanceof ListComponent list) {
            for (final var component : list.getComponents()) render(appender, component, context);
        } else if (input instanceof NumberComponent number) {
            appender.appendText(number.getNumber().toString());
        } else if (input instanceof TemporalComponent temporal) {
            var formatter = temporal.getFallbackFormatter();
            final var locale = context.getMetadata(Audience.LOCALE);
            if (locale.isPresent()) formatter = formatter.withLocale(locale.get());
            appender.appendText(formatter.format(temporal.getTemporal()));
        } else if (input instanceof TranslatableComponent translatable) {
            final var translation = DistributorProvider.get()
                    .getGlobalTranslationSource()
                    .getTranslationOrMissing(
                            translatable.getKey(),
                            context.getMetadata(Audience.LOCALE).orElse(Locale.ENGLISH));
            if (translation instanceof ComponentAwareTranslation aware) {
                appender.appendRawText(aware.format(
                        translatable.getParameters(), new ComponentRendererProcessor(appender.getCurrentStyle())));
            } else {
                appender.appendText(translation.format(translatable.getParameters()));
            }
        }

        appender.appendStyleClose();
    }

    private final class ComponentRendererProcessor implements ComponentAwareTranslation.Processor {

        private final @Nullable ComponentStyle previous;

        private ComponentRendererProcessor(final @Nullable ComponentStyle previous) {
            this.previous = previous;
        }

        @Override
        public String process(final String value, final ComponentStyle style) {
            final var appender = createAppender(previous);
            appender.appendStyleStart(style);
            appender.appendText(value);
            appender.appendStyleClose();
            return appender.toString();
        }
    }

    protected abstract Appender createAppender(final @Nullable ComponentStyle previous);

    public interface Appender {

        void appendStyleStart(final ComponentStyle style);

        void appendText(final String text);

        void appendRawText(final String text);

        void appendStyleClose();

        ComponentStyle getCurrentStyle();

        @Override
        String toString();
    }
}
