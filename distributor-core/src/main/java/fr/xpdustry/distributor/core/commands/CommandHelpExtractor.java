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
package fr.xpdustry.distributor.core.commands;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.CommandHelpHandler.*;
import fr.xpdustry.distributor.api.command.*;
import fr.xpdustry.distributor.api.command.sender.*;
import java.util.*;

public class CommandHelpExtractor {

  private static final Comparator<Entry> SORTER = Comparator.comparing(Entry::name).thenComparing(Entry::syntax);

  public static List<Entry> extract(final CommandHandler handler, final CommandSender sender) {
    final var entries = new ArrayList<Entry>();
    for (final var command : handler.getCommandList()) {
      if (command instanceof ArcCommand<?> arc) {
        final var extracted = extract(sender, arc);
        extracted.sort(SORTER);
        entries.addAll(extracted);
      } else {
        entries.add(new Entry(command.text, command.paramText, command.description));
      }
    }
    return Collections.unmodifiableList(entries);
  }

  private static  <C> List<Entry> extract(final CommandSender sender, final ArcCommand<C> command) {
    final var entries = new ArrayList<Entry>();
    final var helper = command.getManager().createCommandHelpHandler();
    final var caller = command.getManager().getCommandSenderMapper().apply(sender);
    final var topics = new ArrayDeque<HelpTopic<C>>();
    topics.add(helper.queryHelp(caller, command.text));

    while (!topics.isEmpty()) {
      final var topic = topics.remove();

      if (topic instanceof CommandHelpHandler.MultiHelpTopic<?> help) {
        for (final var suggestion : help.getChildSuggestions()) {
          topics.add(helper.queryHelp(caller, suggestion));
        }
      } else if (topic instanceof CommandHelpHandler.VerboseHelpTopic<C> help) {
        final var syntax = command.getManager().commandSyntaxFormatter()
          .apply(help.getCommand().getArguments(), null)
          .replace(command.text + " ", "");
        entries.add(new Entry(command.text, syntax, help.getDescription()));
      } else {
        final var help = (IndexHelpTopic<C>) topic;
        for (final var entry : help.getEntries()) {
          final var syntax = entry.getSyntaxString().replace(command.text + " ", "");
          entries.add(new Entry(command.text, syntax, entry.getDescription()));
        }
      }
    }

    return entries;
  }

  public record Entry(String name, String syntax, String description) {

  }
}
