package org.magmafoundation.magma.permission;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.command.CraftRemoteConsoleCommandSender;
import org.bukkit.craftbukkit.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.magmafoundation.magma.configuration.MagmaConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForgeCommandWrapper extends Command {

    private final Commands dispatcher;
    public final CommandNode<CommandSourceStack> forgeCommand;

    public ForgeCommandWrapper(Commands dispatcher, CommandNode<CommandSourceStack> forgeCommand) {
        super(forgeCommand.getName(), "A Forge modded command.", forgeCommand.getUsageText(), Collections.EMPTY_LIST);
        this.dispatcher = dispatcher;
        this.forgeCommand = forgeCommand;
        this.setPermission(getPermission(forgeCommand));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!MagmaConfig.instance.forgeCommandsIgnoreBukkitPerms.getValues()) {
            //If we detect that the CraftPlayer permissible was injected into (ex. LuckPerms), we will use their permissions
            //instead of the Forge permissions defined in 'Commands.literal(...).requires(s -> s.hasPermission(..))'.
            //This will cause a problem though, as the Forge permissions will not be checked.
            //So you will have to manually set the Forge permissions by allowing ex. "forge.command.coolmodcommand" in LuckPerms.
            boolean permissibleInjected = false;
            if (sender instanceof CraftPlayer player)
//                permissibleInjected = player.isPermissibleInjected(); todo: reimplement

            if ((permissibleInjected && !testPermission(sender)) ||
                    (!permissibleInjected && !forgeCommand.getRequirement().test(getListener(sender))))
                return true;
        } else {
            if (!forgeCommand.getRequirement().test(getListener(sender))) return true;
        }

        CommandSourceStack icommandlistener = getListener(sender);
        dispatcher.setForge(true);
        //add a slash for forge commands, they get removed with stripslash anyway, fixes worldedit
        dispatcher.performPrefixedCommand(icommandlistener, "/"+toDispatcher(args, getName()), "/"+toDispatcher(args, commandLabel));
        dispatcher.setForge(false);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        CommandDispatcher<CommandSourceStack> dispatch = dispatcher.getForgeDispatcher().unwrap();

        CommandSourceStack icommandlistener = getListener(sender);
        ParseResults<CommandSourceStack> parsed = dispatch.parse(toDispatcher(args, getName()), icommandlistener);

        List<String> results = new ArrayList<>();
        dispatch.getCompletionSuggestions(parsed).thenAccept((suggestions) -> {
            suggestions.getList().forEach((s) -> results.add(s.getText()));
        });

        return results;
    }

    public static CommandSourceStack getListener(CommandSender sender) {
        if (sender instanceof Player) {
            return ((CraftPlayer) sender).getHandle().createCommandSourceStack();
        }
        if (sender instanceof BlockCommandSender) {
            return ((CraftBlockCommandSender) sender).getWrapper();
        }
        if (sender instanceof CommandMinecart) {
            return ((MinecartCommandBlock) ((CraftMinecartCommand) sender).getHandle()).getCommandBlock().createCommandSourceStack();
        }
        if (sender instanceof RemoteConsoleCommandSender) {
            return ((CraftRemoteConsoleCommandSender) sender).getListener().createCommandSourceStack();
        }
        if (sender instanceof ConsoleCommandSender) {
            return ((CraftServer) sender.getServer()).getServer().createCommandSourceStack();
        }
        if (sender instanceof ProxiedCommandSender) {
            return ((ProxiedNativeCommandSender) sender).getHandle();
        }

        throw new IllegalArgumentException("Cannot make " + sender + " a forge command listener");
    }

    public static String getPermission(CommandNode<CommandSourceStack> forgeCommand) {
        return "forge.command." + ((forgeCommand.getRedirect() == null) ? forgeCommand.getName() : forgeCommand.getRedirect().getName());
    }

    private String toDispatcher(String[] args, String name) {
        return name + ((args.length > 0) ? " " + Joiner.on(' ').join(args) : "");
    }
}
