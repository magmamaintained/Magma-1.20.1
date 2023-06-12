package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public abstract class CraftSculkSensor extends CraftBlockData implements SculkSensor {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> PHASE = getEnum("sculk_sensor_phase");

    @Override
    public Phase getPhase() {
        return get(PHASE, Phase.class);
    }

    @Override
    public void setPhase(Phase phase) {
        set(PHASE, phase);
    }
}
