package org.bukkit.craftbukkit.v1_20_R1.block;

import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.bukkit.World;
import org.bukkit.block.Conduit;

public class CraftConduit extends CraftBlockEntityState<ConduitBlockEntity> implements Conduit {

    public CraftConduit(World world, ConduitBlockEntity tileEntity) {
        super(world, tileEntity);
    }
}
