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
package com.xpdustry.distributor.api.key;

import com.xpdustry.distributor.api.player.MUUID;
import java.util.Locale;
import mindustry.game.Team;

public final class StandardKeys {

    public static final Key<String> NAME = Key.of(Key.DISTRIBUTOR_NAMESPACE, "name", String.class);
    public static final Key<String> DISPLAY_NAME = Key.of(Key.DISTRIBUTOR_NAMESPACE, "display-name", String.class);
    public static final Key<MUUID> MUUID = Key.of(Key.DISTRIBUTOR_NAMESPACE, "muuid", MUUID.class);
    public static final Key<Locale> LOCALE = Key.of(Key.DISTRIBUTOR_NAMESPACE, "locale", Locale.class);
    public static final Key<Team> TEAM = Key.of(Key.DISTRIBUTOR_NAMESPACE, "team", Team.class);

    private StandardKeys() {}
}
