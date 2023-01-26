/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core.security.permission;

import fr.xpdustry.distributor.core.database.ConnectionFactory;
import java.io.IOException;

public final class Permissibles {

    private Permissibles() {}

    public static void createDatabase(final ConnectionFactory factory) {
        try (final var input = Permissibles.class.getResourceAsStream("/schemas/permission.sql")) {
            if (input == null) {
                throw new IllegalStateException("Missing schema file.");
            }
            factory.executeScript(input);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
