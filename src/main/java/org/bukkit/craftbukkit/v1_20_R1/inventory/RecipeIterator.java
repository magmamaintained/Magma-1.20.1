package org.bukkit.craftbukkit.v1_20_R1.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeType;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.Map;

public class RecipeIterator implements Iterator<Recipe> {
    private final Iterator<Map.Entry<RecipeType<?>, Map<ResourceLocation, net.minecraft.world.item.crafting.Recipe<?>>>> recipes;
    private Iterator<net.minecraft.world.item.crafting.Recipe<?>> current;

    public RecipeIterator() {
        this.recipes = MinecraftServer.getServer().getRecipeManager().recipes.entrySet().iterator();
    }

    @Override
    public boolean hasNext() {
        return (current != null && current.hasNext()) || recipes.hasNext();
    }

    @Override
    public Recipe next() {
        if (current == null || !current.hasNext()) {
            current = recipes.next().getValue().values().iterator();
            return next();
        }

        Recipe next = current.next().toBukkitRecipe();
        return next == null ? next() : next; //Magma - skip recipes that have a null bukkit recipe
    }

    @Override
    public void remove() {
        Preconditions.checkState(current != null, "next() not yet called");

        current.remove();
    }
}
