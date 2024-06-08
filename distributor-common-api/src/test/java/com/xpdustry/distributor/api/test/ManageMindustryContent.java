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
package com.xpdustry.distributor.api.test;

import arc.Core;
import arc.mock.MockSettings;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class ManageMindustryContent implements BeforeAllCallback {

    private static boolean INITIALIZED = false;

    @Override
    public void beforeAll(final ExtensionContext context) {
        if (!INITIALIZED) {
            INITIALIZED = true;
            Vars.content = new ContentLoader();
            Core.settings = new MockSettings();
            Vars.content.createBaseContent();
            Vars.content.init();
        }
    }
}
