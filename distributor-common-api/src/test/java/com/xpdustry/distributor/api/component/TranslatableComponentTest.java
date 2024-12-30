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
package com.xpdustry.distributor.api.component;

import com.xpdustry.distributor.api.key.CTypeKey;
import com.xpdustry.distributor.api.test.ManageMindustryContent;
import mindustry.Vars;
import mindustry.ctype.MappableContent;
import mindustry.game.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ManageMindustryContent.class)
public final class TranslatableComponentTest {

    @Test
    void test_content() {
        for (final var category : CTypeKey.ALL) {
            this.test_content(category);
        }
    }

    <T extends MappableContent> void test_content(final CTypeKey<T> key) {
        for (final var content : Vars.content.<T>getBy(key.getContentType())) {
            final var component = TranslatableComponent.translatable(content);
            assertThat(component.getKey())
                    .isEqualTo("mindustry." + key.getContentType().name() + "." + content.name + ".name");
        }
    }

    @Test
    void test_team() {
        for (final var team : Team.all) {
            final var component = TranslatableComponent.translatable(team);
            if (team.id < Team.baseTeams.length) {
                assertThat(component.getKey()).isEqualTo("mindustry.team." + team.name + ".name");
            } else {
                assertThat(component.getKey()).isEqualTo("distributor.component.team");
            }
        }
    }
}
