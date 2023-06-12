package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.ExplosiveMinecart;

public final class CraftMinecartTNT extends CraftMinecart implements ExplosiveMinecart {
    CraftMinecartTNT(CraftServer server, net.minecraft.world.entity.vehicle.MinecartTNT entity) {
        super(server, entity);
    }

    @Override
    public void setFuseTicks(int ticks) {
        getHandle().fuse = ticks;
    }

    @Override
    public int getFuseTicks() {
        return getHandle().getFuse();
    }

    @Override
    public void ignite() {
        getHandle().primeFuse();
    }

    @Override
    public boolean isIgnited() {
        return getHandle().isPrimed();
    }

    @Override
    public void explode() {
        getHandle().explode(getHandle().getDeltaMovement().horizontalDistanceSqr());
    }

    @Override
    public void explode(double power) {
        Preconditions.checkArgument(0 <= power && power <= 5, "Power must be in range [0, 5] (got %s)", power);

        getHandle().explode(power);
    }

    @Override
    public net.minecraft.world.entity.vehicle.MinecartTNT getHandle() {
        return (net.minecraft.world.entity.vehicle.MinecartTNT) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftMinecartTNT";
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART_TNT;
    }
}
