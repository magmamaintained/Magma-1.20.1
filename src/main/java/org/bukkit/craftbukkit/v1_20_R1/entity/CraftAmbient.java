package org.bukkit.craftbukkit.v1_20_R1.entity;

import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Ambient;

public class CraftAmbient extends CraftMob implements Ambient {
    public CraftAmbient(CraftServer server, net.minecraft.world.entity.ambient.AmbientCreature entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.ambient.AmbientCreature getHandle() {
        return (net.minecraft.world.entity.ambient.AmbientCreature) entity;
    }

    @Override
    public String toString() {
        return "CraftAmbient";
    }

}
