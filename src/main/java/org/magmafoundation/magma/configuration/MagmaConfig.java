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

package org.magmafoundation.magma.configuration;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.commands.ModsCommand;
import org.magmafoundation.magma.configuration.value.Value;
import org.magmafoundation.magma.configuration.value.values.BooleanValue;
import org.magmafoundation.magma.configuration.value.values.IntValue;
import org.magmafoundation.magma.configuration.value.values.StringArrayValue;
import org.magmafoundation.magma.configuration.value.values.StringValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

/**
 * MagmaConfig
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:14 am
 */
public class MagmaConfig extends ConfigBase {

    public static MagmaConfig instance = new MagmaConfig();

    public final BooleanValue forgeBukkitPermissionHandlerEnable = new BooleanValue(this, "forge.bukkitPermissionHandler.enable", true, "Let's Bukkit permission plugins handle forge/modded commands");
    public final BooleanValue forgeCommandsIgnoreBukkitPerms = new BooleanValue(this, "forge.commandsIgnoreBukkitPerms", true, "If true, forge/modded commands will ignore Bukkit permission plugins and use Forge permissions instead, disable this to control forge commands with permission plugins like LuckPerms");
    public final BooleanValue magmaAutoUpdater = new BooleanValue(this, "magma.auto-update", true, "Auto updates the Magma jar");
    public final BooleanValue paperVelocityEnabled = new BooleanValue(this, "paper.velocity.enabled", false, "Enabled support for Velocity modern forwarding");
    public final StringValue paperVelocitySecret = new StringValue(this, "paper.velocity.secret", "", "Velocity Secret Key for modern forwarding");

    public final StringArrayValue fakePlayerPermissions = new StringArrayValue(this, "fakeplayer.permissions", "", "A list of permissions that fake players should have");

    public final BooleanValue modCommandPrintIDs = new BooleanValue(this, "magma.command.mods.print-ids", true, "Adds the mod id + version to the end of the mod name");

    public final BooleanValue debugPrintInjections = new BooleanValue(this, "debug.print-bukkit-injections", false, "Prints Forge Bukkit Injections");
    public final BooleanValue debugWarnOnNullNBT = new BooleanValue(this, "debug.warn-on-null-nbt", false, "Prints a warning when an item tries to set an NBT tag to null");
    public final BooleanValue debugWarnOnUnknownEntity = new BooleanValue(this, "debug.warn-on-unknown-entity", false, "Prints a warning when an entity unknown to bukkit is spawned");

    public final IntValue forgePacketCompressionThreshold = new IntValue(this, "forge.packet-compression-threshold", 8388608, "Maximum packet size before compression is applied, Default: 8388608 (8MB)");

    private final String HEADER = "This is the main configuration file for Magma.\n" +
        "\n" +
        "Site: https://magmafoundation.org\n" +
        "Discord: https://discord.gg/magma\n";


    public MagmaConfig() {
        super("magma.yml", "magma");
        init();
    }

    public static String getString(String s, String key, String defaultreturn) {
        if (s.contains(key)) {
            String string = s.substring(s.indexOf(key));
            String s1 = (string.substring(string.indexOf(": ") + 2));
            String[] ss = s1.split("\n");
            return ss[0].trim().replace("'", "").replace("\"", "");
        }
        return defaultreturn;
    }

    public static String getString(File f, String key, String defaultreturn) {
        try {
            String s = FileUtils.readFileToString(f, "UTF-8");
            if (s.contains(key)) {
                String string = s.substring(s.indexOf(key));
                String s1 = (string.substring(string.indexOf(": ") + 2));
                String[] ss = s1.split("\n");
                return ss[0].trim().replace("'", "").replace("\"", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultreturn;
    }

    public void init() {
        for (Field f : this.getClass().getFields()) {
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
                try {
                    Value value = (Value) f.get(this);
                    if (value == null) {
                        continue;
                    }
                    values.put(value.path, value);
                } catch (ClassCastException e) {
                } catch (Throwable t) {
                    System.out.println("[Magma] Failed to initialize a MagmaConfig values.");
                    t.printStackTrace();
                }
            }
        }
        load();
    }

    @Override
    protected void addCommands() {
        commands.put("mods", new ModsCommand("mods"));
    }

    @Override
    protected void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            StringBuilder header = new StringBuilder(HEADER + "\n");
            for (Value toggle : values.values()) {
                if (!toggle.description.equals("")) {
                    header.append("Value: ").append(toggle.path).append(" Default: ").append(toggle.key).append("   # ").append(toggle.description).append("\n");
                }

                config.addDefault(toggle.path, toggle.key);
                values.get(toggle.path).setValues(config.getString(toggle.path));
            }
            version = getInt("config-version", 1);
            set("config-version", 1);

            config.options().header(header.toString());
            config.options().copyDefaults(true);

            this.save();
        } catch (Exception ex) {
            MinecraftServer.getServer().server.getLogger().log(Level.SEVERE, "Could not load " + this.configFile);
            ex.printStackTrace();
        }
    }
}
