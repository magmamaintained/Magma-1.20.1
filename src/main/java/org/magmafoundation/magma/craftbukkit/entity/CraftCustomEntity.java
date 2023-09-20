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

import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CraftCustomEntity extends CraftEntity {

    private String entityName;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
        if (entityName == null) {
            entityName = entity.getName().getString();
        }
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    public LivingEntity asLivingEntity() {
        try {
            return (LivingEntity) entity;
        } catch (ClassCastException e) {
            System.err.println("Attempted to call asLivingEntity() on a non-LivingEntity entity");
            System.err.println("Entity name: " + entityName);
            System.err.println("Entity type: " + entity.getType());
            System.err.println("Entity class: " + entity.getClass());
            return null;
        }
    }

    @Override
    public String toString() {
        return entityName;
    }

    @Override
    public String getCustomName() {
        String name = this.getHandle().getCustomName().getString();
        return name == null || name.length() == 0 ? this.entity.getName().getString() : name;
    }

}
