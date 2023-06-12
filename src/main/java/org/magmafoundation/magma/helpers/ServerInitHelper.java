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

package org.magmafoundation.magma.helpers;

import com.mojang.serialization.DynamicOps;
import joptsimple.OptionSet;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.DataPackConfig;

public class ServerInitHelper {

    private static OptionSet options;
    private static DataPackConfig dataPackConfig;
    private static DynamicOps<Tag> registryreadops;

    public static void init(OptionSet options, DataPackConfig p_129768_, DynamicOps<Tag> registryreadops) {
        ServerInitHelper.options = options;
        ServerInitHelper.dataPackConfig = p_129768_;
        ServerInitHelper.registryreadops = registryreadops;
    }

    public static OptionSet getOptions() {
        return options;
    }

    public static DataPackConfig getDataPackConfig() {
        return dataPackConfig;
    }

    public static DynamicOps<Tag> getRegistryReadOps() {
        return registryreadops;
    }
}
