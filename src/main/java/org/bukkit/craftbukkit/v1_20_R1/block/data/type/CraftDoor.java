package org.bukkit.craftbukkit.v1_20_R1.block.data.type;

import org.bukkit.block.data.type.Door;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;

public abstract class CraftDoor extends CraftBlockData implements Door {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HINGE = getEnum("hinge");

    @Override
    public Hinge getHinge() {
        return get(HINGE, Hinge.class);
    }

    @Override
    public void setHinge(Hinge hinge) {
        set(HINGE, hinge);
    }
}
