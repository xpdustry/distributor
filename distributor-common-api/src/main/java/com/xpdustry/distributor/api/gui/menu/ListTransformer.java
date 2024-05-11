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

import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.BiAction;
import com.xpdustry.distributor.api.gui.State;
import com.xpdustry.distributor.api.gui.Window;
import com.xpdustry.distributor.api.gui.transform.Transformer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import mindustry.gen.Iconc;

public final class ListTransformer<E> implements Transformer<MenuPane> {

    private Function<Window, List<E>> provider = window -> List.of();
    private Function<E, String> renderer = Object::toString;
    private BiAction<E> choiceAction = BiAction.from(Action.none());
    private int height = 5;
    private int width = 1;
    private boolean fillEmptySpace = false;
    private boolean renderNavigation = true;
    private State.Key<Integer> pageKey = State.Key.of(Integer.class);

    public Function<Window, List<E>> getProvider() {
        return provider;
    }

    public ListTransformer<E> setProvider(final Function<Window, List<E>> provider) {
        this.provider = Objects.requireNonNull(provider);
        return this;
    }

    public Function<E, String> getRenderer() {
        return renderer;
    }

    public ListTransformer<E> setRenderer(final Function<E, String> renderer) {
        this.renderer = Objects.requireNonNull(renderer);
        return this;
    }

    public BiAction<E> getChoiceAction() {
        return choiceAction;
    }

    public ListTransformer<E> setChoiceAction(final BiAction<E> choiceAction) {
        this.choiceAction = Objects.requireNonNull(choiceAction);
        return this;
    }

    public int getHeight() {
        return height;
    }

    public ListTransformer<E> setHeight(final int height) {
        if (height < 1) throw new IllegalArgumentException("Height must be greater than 0");
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ListTransformer<E> setWidth(final int width) {
        if (width < 1) throw new IllegalArgumentException("Width must be greater than 0");
        this.width = width;
        return this;
    }

    public int getPageSize() {
        return height * width;
    }

    public boolean isFillEmptySpace() {
        return fillEmptySpace;
    }

    public ListTransformer<E> setFillEmptySpace(final boolean fillEmptySpace) {
        this.fillEmptySpace = fillEmptySpace;
        return this;
    }

    public boolean isRenderNavigation() {
        return renderNavigation;
    }

    public ListTransformer<E> setRenderNavigation(final boolean renderNavigation) {
        this.renderNavigation = renderNavigation;
        return this;
    }

    public State.Key<Integer> getPageKey() {
        return pageKey;
    }

    public ListTransformer<E> setPageKey(final State.Key<Integer> pageKey) {
        this.pageKey = Objects.requireNonNull(pageKey);
        return this;
    }

    @Override
    public void transform(final MenuPane pane, final Window window) {
        final var elements = provider.apply(window);
        final int page = Math.floorDiv(window.getState().get(pageKey).orElse(0), getPageSize());
        window.getState().put(pageKey, page);

        int cursor = 0;
        for (int i = 0; i < height; i++) {
            final List<MenuOption> options = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                cursor = (page * getPageSize()) + (i * width) + j;
                if (cursor < elements.size()) {
                    final var element = elements.get(cursor);
                    options.add(MenuOption.of(renderer.apply(element), ctx -> choiceAction.act(ctx, element)));
                } else if (this.fillEmptySpace) {
                    options.add(MenuOption.of("", Action.none()));
                } else {
                    break;
                }
            }
            if (!options.isEmpty()) {
                pane.getGrid().addRow(options);
            }
            if (cursor >= elements.size() && !fillEmptySpace) {
                break;
            }
        }

        if (renderNavigation) {
            pane.getGrid()
                    .addRow(
                            createConditionalOption(
                                    page > 0,
                                    String.valueOf(Iconc.left),
                                    Action.of(Action.with(pageKey, page - 1), Window::open)),
                            MenuOption.of(String.valueOf(Iconc.cancel), Action.back()),
                            createConditionalOption(
                                    cursor + 1 < elements.size(),
                                    String.valueOf(Iconc.right),
                                    Action.of(Action.with(pageKey, page + 1), Window::open)));
        }
    }

    private MenuOption createConditionalOption(final boolean condition, final String content, final Action action) {
        return MenuOption.of(condition ? content : "[darkgray]" + content, condition ? action : Action.none());
    }
}
