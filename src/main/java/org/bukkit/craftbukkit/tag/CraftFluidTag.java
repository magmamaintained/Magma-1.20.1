package org.bukkit.craftbukkit.tag;

import net.minecraft.tags.TagKey;
import org.bukkit.Fluid;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.util.Set;
import java.util.stream.Collectors;

public class CraftFluidTag extends CraftTag<net.minecraft.world.level.material.Fluid, Fluid> {

    public CraftFluidTag(net.minecraft.core.Registry<net.minecraft.world.level.material.Fluid> registry, TagKey<net.minecraft.world.level.material.Fluid> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(Fluid fluid) {
        return CraftMagicNumbers.getFluid(fluid).is(tag);
    }

    @Override
    public Set<Fluid> getValues() {
        return getHandle().stream().map((fluid) -> CraftMagicNumbers.getFluid(fluid.value())).collect(Collectors.toUnmodifiableSet());
    }
}
