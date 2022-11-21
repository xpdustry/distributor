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

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.permission.GroupPermissible;
import fr.xpdustry.distributor.api.permission.PermissibleManager;
import fr.xpdustry.distributor.api.permission.PermissionService;
import java.util.Locale;
import java.util.Optional;

public final class GroupPermissibleCommand extends PermissibleCommand<GroupPermissible> {

    public GroupPermissibleCommand(final PermissionService service) {
        super(service);
    }

    @CommandPermission("distributor.permission.group.weight.info")
    @CommandMethod("<group> weight info")
    public void getGroupPermissibleWeight(
            final CommandSender sender,
            final @Argument(value = "group", parserName = "group-parser") GroupPermissible permissible) {
        sender.sendLocalizedMessage("permission.group.weight.get", permissible.getName(), permissible.getWeight());
    }

    @CommandPermission("distributor.permission.group.weight.edit")
    @CommandMethod("<group> weight <weight>")
    public void setGroupPermissibleWeight(
            final CommandSender sender,
            final @Argument(value = "group", parserName = "group-parser") GroupPermissible permissible,
            final @Argument("weight") @Range(min = "0") int weight) {
        if (permissible.getWeight() == weight) {
            sender.sendLocalizedMessage("permission.group.weight.set.already", permissible.getName(), weight);
        } else {
            permissible.setWeight(weight);
            this.getManager().save(permissible);
            sender.sendLocalizedMessage("permission.group.weight.set.success", permissible.getName(), weight);
        }
    }

    @Override
    protected Optional<GroupPermissible> findPermissible(final String input) {
        return this.getManager().findById(input.toLowerCase(Locale.ROOT));
    }

    @Override
    protected PermissibleManager<GroupPermissible> getManager() {
        return this.getPermissionManager().getGroupPermissionManager();
    }
}
