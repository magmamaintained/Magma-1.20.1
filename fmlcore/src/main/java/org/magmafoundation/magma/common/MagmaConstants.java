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

package org.magmafoundation.magma.common;

import net.minecraftforge.forgespi.language.IModInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagmaConstants {

    public static Map<String, Integer> mods = new ConcurrentHashMap<>();
    public static Set<String> modList = new HashSet<>();

    public static List<IModInfo> modInfoList = new ArrayList<>();

    private static final String fullVersion = (MagmaConstants.class.getPackage().getImplementationVersion() != null) ? MagmaConstants.class.getPackage().getImplementationVersion() : "dev-env";

    public static final String NAME = "Magma";
    public static final String BRAND = "MagmaFoundation";
    public static final String VERSION = !Objects.equals(fullVersion, "dev-env") ? fullVersion.split("-")[0] + "-" + fullVersion.split("-")[2] : "dev-env";
    public static final String BUKKIT_VERSION = "v1_20.1_R0";
    public static final String FORGE_VERSION_FULL = fullVersion;
    public static final String FORGE_VERSION = fullVersion.substring(0, fullVersion.lastIndexOf("-") - 1);
    public static final String NMS_PREFIX = "net/minecraft/server/";
    public static final String INSTALLER_LIBRARIES_FOLDER = "libraries";
}
