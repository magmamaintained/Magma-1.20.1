package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
    public CraftCaveSpider(CraftServer server, net.minecraft.world.entity.monster.CaveSpider entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.CaveSpider getHandle() {
        return (net.minecraft.world.entity.monster.CaveSpider) entity;
    }

    @Override
    public String toString() {
        return "CraftCaveSpider";
    }

    @Override
    public EntityType getType() {
        return EntityType.CAVE_SPIDER;
    }
}
