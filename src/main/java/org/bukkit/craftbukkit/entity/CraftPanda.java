package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;

public class CraftPanda extends CraftAnimals implements Panda {

    public CraftPanda(CraftServer server, net.minecraft.world.entity.animal.Panda entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.Panda getHandle() {
        return (net.minecraft.world.entity.animal.Panda) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.PANDA;
    }

    @Override
    public String toString() {
        return "CraftPanda";
    }

    @Override
    public Gene getMainGene() {
        return fromNms(getHandle().getMainGene());
    }

    @Override
    public void setMainGene(Gene gene) {
        getHandle().setMainGene(toNms(gene));
    }

    @Override
    public Gene getHiddenGene() {
        return fromNms(getHandle().getHiddenGene());
    }

    @Override
    public void setHiddenGene(Gene gene) {
        getHandle().setHiddenGene(toNms(gene));
    }

    @Override
    public boolean isRolling() {
        return getHandle().isRolling();
    }

    @Override
    public void setRolling(boolean flag) {
        getHandle().roll(flag);
    }

    @Override
    public boolean isSneezing() {
        return getHandle().isSneezing();
    }

    @Override
    public void setSneezing(boolean flag) {
        getHandle().sneeze(flag);
    }

    @Override
    public boolean isSitting() {
        return getHandle().isSitting();
    }

    @Override
    public void setSitting(boolean flag) {
        getHandle().sit(flag);
    }

    @Override
    public boolean isOnBack() {
        return getHandle().isOnBack();
    }

    @Override
    public void setOnBack(boolean flag) {
        getHandle().setOnBack(flag);
    }

    @Override
    public boolean isEating() {
        return getHandle().isEating();
    }

    @Override
    public void setEating(boolean flag) {
        getHandle().eat(flag);
    }

    @Override
    public boolean isScared() {
        return getHandle().isScared();
    }

    @Override
    public int getUnhappyTicks() {
        return getHandle().getUnhappyCounter();
    }

    public static Gene fromNms(net.minecraft.world.entity.animal.Panda.Gene gene) {
        Preconditions.checkArgument(gene != null, "Gene may not be null");

        return Gene.values()[gene.ordinal()];
    }

    public static net.minecraft.world.entity.animal.Panda.Gene toNms(Gene gene) {
        Preconditions.checkArgument(gene != null, "Gene may not be null");

        return net.minecraft.world.entity.animal.Panda.Gene.values()[gene.ordinal()];
    }
}
