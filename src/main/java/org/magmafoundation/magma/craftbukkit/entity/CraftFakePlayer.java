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

package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraftforge.common.util.FakePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.permissions.Permission;
import org.magmafoundation.magma.configuration.MagmaConfig;

public class CraftFakePlayer extends CraftPlayer {

    public CraftFakePlayer(CraftServer server, FakePlayer entity) {
        super(server, entity);
    }

    @Override
    public boolean hasPermission(String name) {
        return MagmaConfig.instance.fakePlayerPermissions.contains(name) || super.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return MagmaConfig.instance.fakePlayerPermissions.contains(perm.getName()) || super.hasPermission(perm);
    }

    @Override
    public boolean isPermissionSet(String name) {
        return MagmaConfig.instance.fakePlayerPermissions.contains(name) || super.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return MagmaConfig.instance.fakePlayerPermissions.contains(perm.getName()) || super.isPermissionSet(perm);
    }

    public String toString() {
        return "CraftFakePlayer{" + "name=" + getName() + '}';
    }
}
