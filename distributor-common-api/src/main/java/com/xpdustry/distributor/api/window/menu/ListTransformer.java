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
package com.xpdustry.distributor.api.window.menu;

import com.xpdustry.distributor.api.window.Action;
import com.xpdustry.distributor.api.window.BiAction;
import com.xpdustry.distributor.api.window.State;
import com.xpdustry.distributor.api.window.Window;
import com.xpdustry.distributor.api.window.transform.Transformer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import mindustry.gen.Iconc;

public final class ListTransformer<E> implements Transformer<MenuWindow> {

    private Function<Window.Context, List<E>> provider = context -> List.of();
    private Function<E, String> renderer = Object::toString;
    private BiAction<E> choiceAction = (context, element) -> {};
    private int height = 5;
    private int width = 1;
    private boolean fillEmptySpace = false;
    private boolean renderNavigation = true;
    private State.Key<Integer> pageKey = State.Key.of(Integer.class);

    public Function<Window.Context, List<E>> getProvider() {
        return provider;
    }

    public ListTransformer<E> setProvider(final Function<Window.Context, List<E>> provider) {
        this.provider = provider;
        return this;
    }

    public Function<E, String> getRenderer() {
        return renderer;
    }

    public ListTransformer<E> setRenderer(final Function<E, String> renderer) {
        this.renderer = renderer;
        return this;
    }

    public BiAction<E> getChoiceAction() {
        return choiceAction;
    }

    public ListTransformer<E> setChoiceAction(final BiAction<E> choiceAction) {
        this.choiceAction = choiceAction;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public ListTransformer<E> setHeight(final int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ListTransformer<E> setWidth(final int width) {
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
        this.pageKey = pageKey;
        return this;
    }

    @Override
    public void transform(final MenuWindow window, final Window.Context context) {
        final var elements = provider.apply(context);
        final int page = Math.floorDiv(context.getState().get(pageKey).orElse(0), getPageSize());
        context.getState().put(pageKey, page);

        int cursor = 0;
        for (int i = 0; i < height; i++) {
            final List<MenuWindow.Option> options = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                cursor = (page * getPageSize()) + (i * width) + j;
                if (cursor < elements.size()) {
                    final var element = elements.get(cursor);
                    options.add(MenuWindow.Option.of(renderer.apply(element), ctx -> choiceAction.act(ctx, element)));
                } else if (this.fillEmptySpace) {
                    options.add(MenuWindow.Option.of("", Action.none()));
                } else {
                    break;
                }
            }
            if (!options.isEmpty()) {
                window.getGrid().addRow(options);
            }
            if (cursor >= elements.size() && !fillEmptySpace) {
                break;
            }
        }

        if (!renderNavigation) {
            return;
        }

        window.getGrid()
                .addRow(
                        createConditionalOption(
                                page > 0,
                                String.valueOf(Iconc.left),
                                Action.of(Action.with(pageKey, page - 1), Window.Context::open)),
                        MenuWindow.Option.of(String.valueOf(Iconc.cancel), Action.back()),
                        createConditionalOption(
                                cursor + 1 < elements.size(),
                                String.valueOf(Iconc.right),
                                Action.of(Action.with(pageKey, page + 1), Window.Context::open)));
    }

    private MenuWindow.Option createConditionalOption(
            final boolean condition, final String content, final Action action) {
        return MenuWindow.Option.of(condition ? content : "[darkgray]" + content, condition ? action : Action.none());
    }
}
