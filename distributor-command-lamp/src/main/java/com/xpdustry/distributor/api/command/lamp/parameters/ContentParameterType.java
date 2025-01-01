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
package com.xpdustry.distributor.api.command.lamp.parameters;

import com.xpdustry.distributor.api.command.lamp.exception.InvalidContentException;
import com.xpdustry.distributor.api.key.CTypeKey;
import java.util.Locale;
import mindustry.Vars;
import mindustry.ctype.MappableContent;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class ContentParameterType<T extends MappableContent> implements ParameterType<CommandActor, T> {

    private final CTypeKey<T> contentType;

    public ContentParameterType(final CTypeKey<T> contentType) {
        this.contentType = contentType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parse(final MutableStringStream input, final ExecutionContext<CommandActor> context) {
        final var name = input.readString().toLowerCase(Locale.ROOT);
        final var content = Vars.content.getByName(contentType.getContentType(), name);
        if (content == null) {
            throw new InvalidContentException(name, contentType);
        }
        return (T) content;
    }
}
