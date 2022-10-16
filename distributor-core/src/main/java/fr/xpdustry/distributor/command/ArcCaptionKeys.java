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
package fr.xpdustry.distributor.command;

import cloud.commandframework.captions.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * {@link Caption} instances for {@link ArcCommandManager} error messages.
 *
 * @see StandardCaptionKeys
 */
public final class ArcCaptionKeys {

  private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>(7);

  /**
   * Variables: {@code {input}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND = of("argument.parse.failure.player.not_found");

  /**
   * Variables: {@code {input}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY = of("argument.parse.failure.player.too_many");

  /**
   * Variables: {@code {input}}, {@code {teamMode}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_TEAM = of("argument.parse.failure.team");

  /**
   * Variables: {@code {syntax}}.
   */
  public static final Caption COMMAND_INVALID_SYNTAX = of("command.invalid.syntax");

  /**
   * Variables: {@code {permission}}.
   */
  public static final Caption COMMAND_INVALID_PERMISSION = of("command.invalid.permission");

  /**
   * Variables: {@code {command}}.
   */
  public static final Caption COMMAND_FAILURE_NO_SUCH_COMMAND = of("command.failure.no_such_command");

  /**
   * Variables: {@code {message}}.
   */
  public static final Caption COMMAND_FAILURE_EXECUTION = of("command.failure.execution");

  private ArcCaptionKeys() {
  }

  private static Caption of(final String key) {
    final var caption = Caption.of(key);
    RECOGNIZED_CAPTIONS.add(caption);
    return caption;
  }

  /**
   * Returns an unmodifiable view of all the captions used in the {@link ArcCommandManager}.
   */
  public static @UnmodifiableView @NotNull Collection<Caption> getCaptionKeys() {
    return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
  }
}
