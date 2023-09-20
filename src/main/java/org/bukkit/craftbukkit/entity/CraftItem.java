package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CraftItem extends CraftEntity implements Item {
    private final ItemEntity item;

    public CraftItem(CraftServer server, Entity entity, ItemEntity item) {
        super(server, entity);
        this.item = item;
    }

    public CraftItem(CraftServer server, ItemEntity entity) {
        this(server, entity, entity);
    }

    @Override
    public ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(item.getItem());
    }

    @Override
    public void setItemStack(ItemStack stack) {
        item.setItem(CraftItemStack.asNMSCopy(stack));
    }

    @Override
    public int getPickupDelay() {
        return item.pickupDelay;
    }

    @Override
    public void setPickupDelay(int delay) {
        item.pickupDelay = Math.min(delay, Short.MAX_VALUE);
    }

    @Override
    public void setUnlimitedLifetime(boolean unlimited) {
        if (unlimited) {
            // See EntityItem#INFINITE_LIFETIME
            item.age = Short.MIN_VALUE;
        } else {
            item.age = getTicksLived();
        }
    }

    @Override
    public boolean isUnlimitedLifetime() {
        return item.age == Short.MIN_VALUE;
    }

    @Override
    public void setTicksLived(int value) {
        super.setTicksLived(value);

        // Second field for net.minecraft.world.entity.item.ItemEntity (don't set if lifetime is unlimited)
        if (!isUnlimitedLifetime()) {
            item.age = value;
        }
    }

    @Override
    public void setOwner(UUID uuid) {
        item.setTarget(uuid);
    }

    @Override
    public UUID getOwner() {
        return item.target;
    }

    @Override
    public void setThrower(UUID uuid) {
        item.setThrower(uuid);
    }

    @Override
    public UUID getThrower() {
        return item.thrower;
    }

    @Override
    public String toString() {
        return "CraftItem";
    }
}
