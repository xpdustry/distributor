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
package com.xpdustry.distributor.api.gui.input;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.ComponentLike;
import com.xpdustry.distributor.api.gui.Action;
import com.xpdustry.distributor.api.gui.BiAction;
import com.xpdustry.distributor.api.gui.Pane;

public interface TextInputPane extends Pane {

    static TextInputPane create() {
        return new TextInputPaneImpl();
    }

    Component getTitle();

    TextInputPane setTitle(final ComponentLike title);

    default TextInputPane setTitle(final String title) {
        return setTitle(DistributorProvider.get().getMindustryComponentDecoder().decode(title));
    }

    Component getDescription();

    TextInputPane setDescription(final ComponentLike description);

    default TextInputPane setDescription(final String description) {
        return setDescription(
                DistributorProvider.get().getMindustryComponentDecoder().decode(description));
    }

    Component getPlaceholder();

    TextInputPane setPlaceholder(final ComponentLike placeholder);

    default TextInputPane setPlaceholder(final String placeholder) {
        return setPlaceholder(
                DistributorProvider.get().getMindustryComponentDecoder().decode(placeholder));
    }

    int getMaxLength();

    TextInputPane setMaxLength(final int maxLength);

    BiAction<String> getInputAction();

    TextInputPane setInputAction(final BiAction<String> inputAction);

    Action getExitAction();

    TextInputPane setExitAction(final Action exitAction);
}
