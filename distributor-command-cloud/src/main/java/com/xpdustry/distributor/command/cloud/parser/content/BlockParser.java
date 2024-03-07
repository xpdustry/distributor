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
package com.xpdustry.distributor.command.cloud.parser.content;

import mindustry.ctype.ContentType;
import mindustry.world.Block;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.parser.ParserDescriptor;

public final class BlockParser<C> extends ContentParser<C, Block> {

    public static <C> ParserDescriptor<C, Block> blockParser() {
        return ParserDescriptor.of(new BlockParser<>(), Block.class);
    }

    public static <C> CommandComponent.Builder<C, Block> blockComponent() {
        return CommandComponent.<C, Block>builder().parser(blockParser());
    }

    public BlockParser() {
        super(ContentType.block);
    }
}
