package org.magmafoundation.magma.craftbukkit.entity;

import net.minecraft.world.entity.raid.Raider;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftRaider;

public class CraftCustomRaider extends CraftRaider {

    public CraftCustomRaider(CraftServer server, Raider entity) {
        super(server, entity);
    }

    public String toString() {
        return "CraftCustomRaider";
    }
}
