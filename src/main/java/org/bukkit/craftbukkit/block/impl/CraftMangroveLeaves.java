/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftMangroveLeaves extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.Leaves, org.bukkit.block.data.Waterlogged {

    public CraftMangroveLeaves() {
        super();
    }

    public CraftMangroveLeaves(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftLeaves

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty DISTANCE = getInteger(net.minecraft.world.level.block.MangroveLeavesBlock.class, "distance");
    private static final net.minecraft.world.level.block.state.properties.BooleanProperty PERSISTENT = getBoolean(net.minecraft.world.level.block.MangroveLeavesBlock.class, "persistent");

    @Override
    public boolean isPersistent() {
        return get(PERSISTENT);
    }

    @Override
    public void setPersistent(boolean persistent) {
        set(PERSISTENT, persistent);
    }

    @Override
    public int getDistance() {
        return get(DISTANCE);
    }

    @Override
    public void setDistance(int distance) {
        set(DISTANCE, distance);
    }

    // org.bukkit.craftbukkit.block.data.CraftWaterlogged

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.MangroveLeavesBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(WATERLOGGED, waterlogged);
    }
}
