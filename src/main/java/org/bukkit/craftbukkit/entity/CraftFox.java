package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;

import java.util.Optional;
import java.util.UUID;

public class CraftFox extends CraftAnimals implements Fox {

    public CraftFox(CraftServer server, net.minecraft.world.entity.animal.Fox entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.Fox getHandle() {
        return (net.minecraft.world.entity.animal.Fox) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.FOX;
    }

    @Override
    public String toString() {
        return "CraftFox";
    }

    @Override
    public Type getFoxType() {
        return Type.values()[getHandle().getVariant().ordinal()];
    }

    @Override
    public void setFoxType(Type type) {
        Preconditions.checkArgument(type != null, "type");

        getHandle().setVariant(net.minecraft.world.entity.animal.Fox.Type.values()[type.ordinal()]);
    }

    @Override
    public boolean isCrouching() {
        return getHandle().isCrouching();
    }

    @Override
    public void setCrouching(boolean crouching) {
        getHandle().setIsCrouching(crouching);
    }

    @Override
    public boolean isSitting() {
        return getHandle().isSitting();
    }

    @Override
    public void setSitting(boolean sitting) {
        getHandle().setSitting(sitting);
    }

    @Override
    public void setSleeping(boolean sleeping) {
        getHandle().setSleeping(sleeping);
    }

    @Override
    public AnimalTamer getFirstTrustedPlayer() {
        UUID uuid = getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0).orElse(null);
        if (uuid == null) {
            return null;
        }

        AnimalTamer player = getServer().getPlayer(uuid);
        if (player == null) {
            player = getServer().getOfflinePlayer(uuid);
        }

        return player;
    }

    @Override
    public void setFirstTrustedPlayer(AnimalTamer player) {
        if (player == null && getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1).isPresent()) {
            throw new IllegalStateException("Must remove second trusted player first");
        }

        getHandle().getEntityData().set(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0, player == null ? Optional.empty() : Optional.of(player.getUniqueId()));
    }

    @Override
    public AnimalTamer getSecondTrustedPlayer() {
        UUID uuid = getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1).orElse(null);
        if (uuid == null) {
            return null;
        }

        AnimalTamer player = getServer().getPlayer(uuid);
        if (player == null) {
            player = getServer().getOfflinePlayer(uuid);
        }

        return player;
    }

    @Override
    public void setSecondTrustedPlayer(AnimalTamer player) {
        if (player != null && !getHandle().getEntityData().get(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_0).isPresent()) {
            throw new IllegalStateException("Must add first trusted player first");
        }

        getHandle().getEntityData().set(net.minecraft.world.entity.animal.Fox.DATA_TRUSTED_ID_1, player == null ? Optional.empty() : Optional.of(player.getUniqueId()));
    }

    @Override
    public boolean isFaceplanted() {
        return getHandle().isFaceplanted();
    }
}
