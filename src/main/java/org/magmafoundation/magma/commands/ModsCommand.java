/*
 * Magma Server
 * Copyright (C) 2019-2023.
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

package org.magmafoundation.magma.commands;

import net.minecraftforge.forgespi.language.IModInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.common.MagmaConstants;
import org.magmafoundation.magma.configuration.MagmaConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModsCommand extends BukkitCommand {

    public ModsCommand(@NotNull String name) {
        super(name);
        this.description = "Gets a list of mods running on the server";
        this.usageMessage = "/mods";
        this.setPermission("magma.command.mods");
        this.setAliases(Arrays.asList("modlist"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) {
        if (!testPermission(sender)) return true;

        sender.sendMessage("Mods " + getModList());
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    @NotNull
    private String getModList() {
        StringBuilder modList = new StringBuilder();
        List<IModInfo> mods = MagmaConstants.modInfoList;

        for (IModInfo mod : mods) {
            if (modList.length() > 0) {
                modList.append(ChatColor.WHITE);
                modList.append(", ");
            }

            modList.append(ChatColor.GREEN + mod.getDisplayName());
            if (MagmaConfig.instance.modCommandPrintIDs.getValues())
                modList.append(ChatColor.WHITE + " (" + mod.getModId() + " v." + mod.getVersion() + ")");
        }

        return "(" + mods.size() + "): " + modList.toString();
    }
}
