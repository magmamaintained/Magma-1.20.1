/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftStairs extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.Stairs, org.bukkit.block.data.Bisected, org.bukkit.block.data.Directional, org.bukkit.block.data.Waterlogged {

    public CraftStairs() {
        super();
    }

    public CraftStairs(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftStairs

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum(net.minecraft.world.level.block.StairBlock.class, "shape");

    @Override
    public Shape getShape() {
        return get(SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        set(SHAPE, shape);
    }

    // org.bukkit.craftbukkit.block.data.CraftBisected

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> HALF = getEnum(net.minecraft.world.level.block.StairBlock.class, "half");

    @Override
    public Half getHalf() {
        return get(HALF, Half.class);
    }

    @Override
    public void setHalf(Half half) {
        set(HALF, half);
    }

    // org.bukkit.craftbukkit.block.data.CraftDirectional

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> FACING = getEnum(net.minecraft.world.level.block.StairBlock.class, "facing");

    @Override
    public org.bukkit.block.BlockFace getFacing() {
        return get(FACING, org.bukkit.block.BlockFace.class);
    }

    @Override
    public void setFacing(org.bukkit.block.BlockFace facing) {
        set(FACING, facing);
    }

    @Override
    public java.util.Set<org.bukkit.block.BlockFace> getFaces() {
        return getValues(FACING, org.bukkit.block.BlockFace.class);
    }

    // org.bukkit.craftbukkit.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.StairBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(WATERLOGGED, waterlogged);
    }
}
