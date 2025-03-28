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
package com.xpdustry.distributor.api.gui.menu;

import arc.graphics.Color;
import arc.math.Mathf;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.TextStyle;
import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.BiAction;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.gui.transform.Transformer;
import com.xpdustry.distributor.api.key.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import mindustry.gen.Iconc;

import static com.xpdustry.distributor.api.component.TextComponent.text;

/**
 * A transformer that renders a list of elements in a menu.
 *
 * @param <E> the type of the elements
 */
public final class ListTransformer<E> implements Transformer<MenuPane> {

    private static final TextStyle DISABLED = TextStyle.of(ComponentColor.from(Color.darkGray));

    private Function<Context<MenuPane>, List<E>> provider = ctx -> List.of();
    private BiFunction<Context<MenuPane>, E, Component> renderer = (ctx, e) -> text(e.toString());
    private BiAction<E> choiceAction = BiAction.from(Action.none());
    private int height = 5;
    private int width = 1;
    private boolean fillEmptyWidth = false;
    private boolean fillEmptyHeight = false;
    private boolean renderNavigation = true;
    private Key<Integer> pageKey = Key.generated(Integer.class);

    /**
     * Returns the provider of the elements.
     */
    public Function<Context<MenuPane>, List<E>> getProvider() {
        return this.provider;
    }

    /**
     * Sets the provider of the elements.
     *
     * @param provider the provider
     * @return this transformer
     */
    public ListTransformer<E> setProvider(final Function<Context<MenuPane>, List<E>> provider) {
        this.provider = Objects.requireNonNull(provider);
        return this;
    }

    /**
     * Returns the renderer of the elements.
     */
    public BiFunction<Context<MenuPane>, E, Component> getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the renderer of the elements.
     *
     * @param renderer the renderer
     * @return this transformer
     */
    public ListTransformer<E> setRenderer(final Function<E, Component> renderer) {
        return this.setRenderer((ctx, e) -> renderer.apply(e));
    }

    /**
     * Sets the renderer of the elements.
     *
     * @param renderer the renderer
     * @return this transformer
     */
    public ListTransformer<E> setRenderer(final BiFunction<Context<MenuPane>, E, Component> renderer) {
        this.renderer = Objects.requireNonNull(renderer);
        return this;
    }

    /**
     * Returns the action to perform when an element is chosen.
     */
    public BiAction<E> getChoiceAction() {
        return this.choiceAction;
    }

    /**
     * Sets the action to perform when an element is chosen.
     *
     * @param choiceAction the action
     * @return this transformer
     */
    public ListTransformer<E> setChoiceAction(final BiAction<E> choiceAction) {
        this.choiceAction = Objects.requireNonNull(choiceAction);
        return this;
    }

    /**
     * Returns the height of a page.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Sets the height of a page.
     *
     * @param height the height
     * @return this transformer
     */
    public ListTransformer<E> setHeight(final int height) {
        if (height < 1) throw new IllegalArgumentException("Height must be greater than 0");
        this.height = height;
        return this;
    }

    /**
     * Returns the width of a page.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the width of a page.
     *
     * @param width the width
     * @return this transformer
     */
    public ListTransformer<E> setWidth(final int width) {
        if (width < 1) throw new IllegalArgumentException("Width must be greater than 0");
        this.width = width;
        return this;
    }

    /**
     * Returns the page size.
     */
    public int getPageSize() {
        return this.height * this.width;
    }

    /**
     * Returns whether the transformer fills empty spaces.
     * @deprecated Use {@link #isFillEmptyWidth()} or {@link #isFillEmptyHeight()}
     */
    @Deprecated(forRemoval = true)
    public boolean isFillEmptySpace() {
        return this.fillEmptyWidth && this.fillEmptyHeight;
    }

    /**
     * Sets whether the transformer fills empty spaces.
     * <p>
     * For example, if the height is 10 and the width is 1, and there are 7 elements,
     * the transformer will add 3 empty options to fill the page.
     *
     * @param fillEmptySpace whether to fill empty spaces
     * @return this transformer
     * @deprecated Use {@link #setFillEmptyWidth(boolean)} or {@link #setFillEmptyHeight(boolean)}
     */
    @Deprecated(forRemoval = true)
    public ListTransformer<E> setFillEmptySpace(final boolean fillEmptySpace) {
        this.fillEmptyWidth = fillEmptySpace;
        this.fillEmptyHeight = fillEmptySpace;
        return this;
    }

    /**
     * Returns whether the transformer fills empty spaces across the width of the list.
     */
    public boolean isFillEmptyWidth() {
        return this.fillEmptyWidth;
    }

    /**
     * Sets whether the transformer fills empty spaces across the width of the list.
     * <p>
     * For example, if the height is 12 and the width is 3, and there are 7 elements,
     * the transformer will add 2 empty options to fill the 3rd row.
     * An empty 4th row won't be added unless {@link #isFillEmptyHeight()} is {@code true}.
     *
     * @param fillEmptyWidth whether to fill empty spaces
     * @return this transformer
     */
    public ListTransformer<E> setFillEmptyWidth(final boolean fillEmptyWidth) {
        this.fillEmptyWidth = fillEmptyWidth;
        return this;
    }

    /**
     * Returns whether the transformer fills empty spaces across the height of the list.
     */
    public boolean isFillEmptyHeight() {
        return this.fillEmptyHeight;
    }

    /**
     * Sets whether the transformer fills empty spaces across the height of the list.
     * <p>
     * For example, if the height is 12 and the width is 3, and there are 7 elements,
     * the transformer will add an empty 4th row.
     * The 3 empty options in the 4th row and the 2 empty options in the 3rd row won't be added
     * unless {@link #isFillEmptyWidth()} is {@code true}.
     *
     * @param fillEmptyHeight whether to fill empty spaces
     * @return this transformer
     */
    public ListTransformer<E> setFillEmptyHeight(final boolean fillEmptyHeight) {
        this.fillEmptyHeight = fillEmptyHeight;
        return this;
    }

    /**
     * Returns whether the navigation buttons are rendered.
     */
    public boolean isRenderNavigation() {
        return this.renderNavigation;
    }

    /**
     * Sets whether the navigation buttons are rendered.
     * <p>
     * A previous button, a page indicator, and a next button are rendered.
     *
     * @param renderNavigation whether to render navigation buttons
     * @return this transformer
     */
    public ListTransformer<E> setRenderNavigation(final boolean renderNavigation) {
        this.renderNavigation = renderNavigation;
        return this;
    }

    /**
     * Returns the key used to store the current page.
     */
    public Key<Integer> getPageKey() {
        return this.pageKey;
    }

    /**
     * Sets the key used to store the current page.
     *
     * @param pageKey the page key
     * @return this transformer
     */
    public ListTransformer<E> setPageKey(final Key<Integer> pageKey) {
        this.pageKey = Objects.requireNonNull(pageKey);
        return this;
    }

    @Override
    public void transform(final Context<MenuPane> context) {
        final var elements = this.provider.apply(context);
        final int pages = Math.floorDiv(elements.size(), this.getPageSize())
                + (elements.size() % this.getPageSize() == 0 ? 0 : 1);
        final int page = context.getState().getOptional(this.pageKey).orElse(0);

        int cursor = 0;
        for (int i = 0; i < this.height; i++) {
            final List<MenuOption> options = new ArrayList<>();
            for (int j = 0; j < this.width; j++) {
                cursor = (page * this.getPageSize()) + (i * this.width) + j;
                if (cursor < elements.size()) {
                    final var element = elements.get(cursor);
                    options.add(MenuOption.of(
                            this.renderer.apply(context, element), ctx -> this.choiceAction.act(ctx, element)));
                } else if (this.fillEmptyWidth) {
                    options.add(MenuOption.of());
                } else {
                    break;
                }
            }
            if (!options.isEmpty()) {
                context.getPane().getGrid().addRow(options);
            }
            if (cursor >= elements.size() && !this.fillEmptyHeight) {
                break;
            }
        }

        if (this.renderNavigation) {
            context.getPane()
                    .getGrid()
                    .addRow(
                            this.createConditionalOption(
                                    page > 0,
                                    Iconc.left,
                                    Action.with(this.pageKey, page - 1).then(Window::show)),
                            MenuOption.of(Mathf.clamp(page + 1, 0, pages) + " / " + pages, Action.none()),
                            this.createConditionalOption(
                                    cursor + 1 < elements.size(),
                                    Iconc.right,
                                    Action.with(this.pageKey, page + 1).then(Window::show)));
        }
    }

    private MenuOption createConditionalOption(final boolean condition, final char icon, final Action action) {
        return MenuOption.of(text(icon, condition ? TextStyle.of() : DISABLED), condition ? action : Action.none());
    }
}
