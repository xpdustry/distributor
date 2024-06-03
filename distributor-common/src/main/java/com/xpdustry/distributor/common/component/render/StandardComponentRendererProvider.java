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
package com.xpdustry.distributor.common.component.render;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ListComponent;
import com.xpdustry.distributor.api.component.NumberComponent;
import com.xpdustry.distributor.api.component.TemporalComponent;
import com.xpdustry.distributor.api.component.TextComponent;
import com.xpdustry.distributor.api.component.TranslatableComponent;
import com.xpdustry.distributor.api.component.render.ComponentRenderer;
import com.xpdustry.distributor.api.component.render.ComponentRendererProvider;
import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;
import com.xpdustry.distributor.api.component.style.TemporalStyle;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.translation.ComponentAwareTranslation;
import java.time.ZoneId;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StandardComponentRendererProvider implements ComponentRendererProvider {

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends Component> ComponentRenderer<T> getRenderer(final T component) {
        if (component instanceof TextComponent) {
            return (ComponentRenderer<T>) TextComponentRenderer.INSTANCE;
        } else if (component instanceof ListComponent) {
            return (ComponentRenderer<T>) ListComponentRenderer.INSTANCE;
        } else if (component instanceof TemporalComponent) {
            return (ComponentRenderer<T>) TemporalComponentRenderer.INSTANCE;
        } else if (component instanceof TranslatableComponent) {
            return (ComponentRenderer<T>) TranslatableComponentRenderer.INSTANCE;
        } else if (component instanceof NumberComponent) {
            return (ComponentRenderer<T>) NumberComponentRenderer.INSTANCE;
        } else {
            return null;
        }
    }

    private static final class TextComponentRenderer implements ComponentRenderer<TextComponent> {

        private static final TextComponentRenderer INSTANCE = new TextComponentRenderer();

        @Override
        public void render(final TextComponent component, final ComponentStringBuilder builder) {
            builder.append(component.getContent());
        }
    }

    private static final class ListComponentRenderer implements ComponentRenderer<ListComponent> {

        private static final ListComponentRenderer INSTANCE = new ListComponentRenderer();

        @Override
        public void render(final ListComponent component, final ComponentStringBuilder builder) {
            for (final var child : component.getComponents()) {
                builder.append(child);
            }
        }
    }

    private static final class TemporalComponentRenderer implements ComponentRenderer<TemporalComponent> {

        private static final TemporalComponentRenderer INSTANCE = new TemporalComponentRenderer();

        @Override
        public void render(final TemporalComponent component, final ComponentStringBuilder builder) {
            if (component.getTemporalStyle() instanceof TemporalStyle.DateTime format) {
                format.toFormatter()
                        .withZone(ZoneId.of("UTC"))
                        .withLocale(builder.getContext()
                                .getMetadata(StandardKeys.LOCALE)
                                .orElseGet(Locale::getDefault))
                        .formatTo(component.getTemporal(), builder);
            } else if (component.getTemporalStyle() instanceof TemporalStyle.None) {
                builder.append(component.getTemporal().toString());
            } else {
                throw new IllegalArgumentException("Unsupported temporal format: " + component.getTemporalStyle());
            }
        }
    }

    private static final class TranslatableComponentRenderer implements ComponentRenderer<TranslatableComponent> {

        private static final TranslatableComponentRenderer INSTANCE = new TranslatableComponentRenderer();

        @Override
        public void render(final TranslatableComponent component, final ComponentStringBuilder builder) {
            final var translation = DistributorProvider.get()
                    .getGlobalTranslationSource()
                    .getTranslationOrMissing(
                            component.getKey(),
                            builder.getContext()
                                    .getMetadata(StandardKeys.LOCALE)
                                    .orElseGet(Locale::getDefault));
            if (translation instanceof ComponentAwareTranslation aware) {
                aware.formatTo(component.getParameters(), builder);
            } else {
                builder.append(translation.format(component.getParameters()));
            }
        }
    }

    private static final class NumberComponentRenderer implements ComponentRenderer<NumberComponent> {

        private static final NumberComponentRenderer INSTANCE = new NumberComponentRenderer();

        @Override
        public void render(final NumberComponent component, final ComponentStringBuilder builder) {
            builder.append(component.getNumber().toString());
        }
    }
}
