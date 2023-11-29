package org.bukkit.craftbukkit.v1_20_R1.block.data;

import org.bukkit.block.data.Bisected;

public class CraftBisected extends CraftBlockData implements Bisected {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HALF = getEnum("half");

    @Override
    public Half getHalf() {
        return get(HALF, Half.class);
    }

    @Override
    public void setHalf(Half half) {
        set(HALF, half);
    }
}
