package org.bukkit.craftbukkit.tag;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.util.Set;
import java.util.stream.Collectors;

public class CraftBlockTag extends CraftTag<Block, Material> {

    public CraftBlockTag(Registry<Block> registry, TagKey<Block> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Material item) {
        Block block = CraftMagicNumbers.getBlock(item);

        // SPIGOT-6952: A Material is not necessary a block, in this case return false
        if (block == null) {
            return false;
        }

        return block.builtInRegistryHolder().is(tag);
    }

    @Override
    public Set<Material> getValues() {
        return getHandle().stream().map((block) -> CraftMagicNumbers.getMaterial(block.value())).collect(Collectors.toUnmodifiableSet());
    }
}
