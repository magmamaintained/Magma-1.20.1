package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftPointedDripstone extends CraftBlockData implements PointedDripstone {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> VERTICAL_DIRECTION = getEnum("vertical_direction");
    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> THICKNESS = getEnum("thickness");

    @Override
    public org.bukkit.block.BlockFace getVerticalDirection() {
        return get(VERTICAL_DIRECTION, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setVerticalDirection(org.bukkit.block.BlockFace direction) {
        set(VERTICAL_DIRECTION, direction);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getVerticalDirections() {
        return getValues(VERTICAL_DIRECTION, org.bukkit.block.BlockFace.class);
    }

    @Override
    public Thickness getThickness() {
        return get(THICKNESS, Thickness.class);
    }

    @Override
    public void setThickness(Thickness thickness) {
        set(THICKNESS, thickness);
    }
}
