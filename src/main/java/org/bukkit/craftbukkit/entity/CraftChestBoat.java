package org.bukkit.craftbukkit.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.loot.LootTable;

public class CraftChestBoat extends CraftBoat implements org.bukkit.entity.ChestBoat {

    private final Inventory inventory;

    public CraftChestBoat(CraftServer server, ChestBoat entity) {
        super(server, entity);
        inventory = new CraftInventory(entity);
    }

    @Override
    public ChestBoat getHandle() {
        return (ChestBoat) entity;
    }

    @Override
    public String toString() {
        return "CraftChestBoat";
    }

    @Override
    public EntityType getType() {
        return EntityType.CHEST_BOAT;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void setLootTable(LootTable table) {
        setLootTable(table, getSeed());
    }

    @Override
    public LootTable getLootTable() {
        ResourceLocation nmsTable = getHandle().getLootTable();
        if (nmsTable == null) {
            return null; // return empty loot table?
        }

        NamespacedKey key = CraftNamespacedKey.fromMinecraft(nmsTable);
        return Bukkit.getLootTable(key);
    }

    @Override
    public void setSeed(long seed) {
        setLootTable(getLootTable(), seed);
    }

    @Override
    public long getSeed() {
        return getHandle().getLootTableSeed();
    }

    private void setLootTable(LootTable table, long seed) {
        ResourceLocation newKey = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
        getHandle().setLootTable(newKey);
        getHandle().setLootTableSeed(seed);
    }
}
