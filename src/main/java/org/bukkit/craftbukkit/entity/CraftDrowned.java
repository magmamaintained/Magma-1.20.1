package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;

public class CraftDrowned extends CraftZombie implements Drowned {

    public CraftDrowned(CraftServer server, net.minecraft.world.entity.monster.Drowned entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.Drowned getHandle() {
        return (net.minecraft.world.entity.monster.Drowned) entity;
    }

    @Override
    public String toString() {
        return "CraftDrowned";
    }

    @Override
    public EntityType getType() {
        return EntityType.DROWNED;
    }
}
