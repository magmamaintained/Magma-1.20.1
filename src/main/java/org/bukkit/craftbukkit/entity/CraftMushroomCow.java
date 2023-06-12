package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

public class CraftMushroomCow extends CraftCow implements MushroomCow {
    public CraftMushroomCow(CraftServer server, net.minecraft.world.entity.animal.MushroomCow entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.MushroomCow getHandle() {
        return (net.minecraft.world.entity.animal.MushroomCow) entity;
    }

    @Override
    public Variant getVariant() {
        return Variant.values()[getHandle().getVariant().ordinal()];
    }

    @Override
    public void setVariant(Variant variant) {
        Preconditions.checkArgument(variant != null, "variant");

        getHandle().setVariant(net.minecraft.world.entity.animal.MushroomCow.MushroomType.values()[variant.ordinal()]);
    }

    @Override
    public String toString() {
        return "CraftMushroomCow";
    }

    @Override
    public EntityType getType() {
        return EntityType.MUSHROOM_COW;
    }
}
