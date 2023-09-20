package org.bukkit.craftbukkit.entity;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;

public class CraftRabbit extends CraftAnimals implements Rabbit {

    public CraftRabbit(CraftServer server, net.minecraft.world.entity.animal.Rabbit entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.Rabbit getHandle() {
        return (net.minecraft.world.entity.animal.Rabbit) entity;
    }

    @Override
    public String toString() {
        return "CraftRabbit{RabbitType=" + getRabbitType() + "}";
    }


    @Override
    public Type getRabbitType() {
        return Type.values()[getHandle().getVariant().ordinal()];
    }

    @Override
    public void setRabbitType(Type type) {
        getHandle().setVariant(net.minecraft.world.entity.animal.Rabbit.Variant.values()[type.ordinal()]);
    }
}
