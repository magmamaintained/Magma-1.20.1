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

package org.magmafoundation.magma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.magmafoundation.magma.patcher.PatcherManager;

import static org.magmafoundation.magma.common.MagmaConstants.*;

/**
 * Magma
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 28/06/2020 - 12:32 am
 */
public class Magma {

    public static Logger LOGGER = LogManager.getLogger(Magma.class);

    private static Magma INSTANCE = new Magma();
    private PatcherManager patcherManager;

    public Magma() {
        INSTANCE = this;
    }

    public static Magma getInstance() {
        return INSTANCE;
    }

    public static String getName() {
        return NAME;
    }

    public static String getBrand() {
        return BRAND;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getBukkitVersion() {
        return BUKKIT_VERSION;
    }

    public static String getForgeVersion() {
        return FORGE_VERSION;
    }

    public static String getNmsPrefix() {
        return NMS_PREFIX;
    }

    public PatcherManager getPatcherManager() {
        return patcherManager;
    }

    public void setPatcherManager(PatcherManager patcherManager) {
        this.patcherManager = patcherManager;
    }
}
