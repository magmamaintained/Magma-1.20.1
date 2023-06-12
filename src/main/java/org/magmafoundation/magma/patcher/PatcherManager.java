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

package org.magmafoundation.magma.patcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.magmafoundation.magma.patcher.impl.WorldEditPatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * PatcherManager
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 05/03/2020 - 08:52 pm
 */
public class PatcherManager {

    private final List<Patcher> patcherList = new ArrayList<>();
    public static final Logger LOGGER = LogManager.getLogger();

    public void init() {
        initPatches();
        patcherList.forEach(patcher -> LOGGER.info("{} [{}] loaded", patcher.getName(), patcher.getDescription()));
        LOGGER.info("{} patches loaded!", patcherList.size());
    }

    private void initPatches() {
        //TODO: find a way to fix Reflections not working with Magma 1.18
        patcherList.add(new WorldEditPatcher());
    }

    public List<Patcher> getPatcherList() {
        return patcherList;
    }

    public <T extends Patcher> Patcher getPatchByClass(final Class<T> clazz) {
        return patcherList.stream().filter(patcher -> patcher.getClass().equals(clazz)).findFirst().map(clazz::cast).orElse(null);
    }

    public Patcher getPatchByName(final String patchName) {
        return patcherList.stream().filter(patcher -> patcher.getName().toLowerCase().replaceAll(" ", "").equalsIgnoreCase(patchName)).findFirst().orElse(null);
    }
}
