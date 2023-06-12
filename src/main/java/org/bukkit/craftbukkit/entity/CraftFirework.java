package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class CraftFirework extends CraftProjectile implements Firework {

    private final Random random = new Random();
    private final CraftItemStack item;

    public CraftFirework(CraftServer server, FireworkRocketEntity entity) {
        super(server, entity);

        ItemStack item = getHandle().getEntityData().get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);

        if (item.isEmpty()) {
            item = new ItemStack(Items.FIREWORK_ROCKET);
            getHandle().getEntityData().set(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, item);
        }

        this.item = CraftItemStack.asCraftMirror(item);

        // Ensure the item is a firework...
        if (this.item.getType() != Material.FIREWORK_ROCKET) {
            this.item.setType(Material.FIREWORK_ROCKET);
        }
    }

    @Override
    public FireworkRocketEntity getHandle() {
        return (FireworkRocketEntity) entity;
    }

    @Override
    public String toString() {
        return "CraftFirework";
    }

    @Override
    public EntityType getType() {
        return EntityType.FIREWORK;
    }

    @Override
    public FireworkMeta getFireworkMeta() {
        return (FireworkMeta) item.getItemMeta();
    }

    @Override
    public void setFireworkMeta(FireworkMeta meta) {
        item.setItemMeta(meta);

        // Copied from FireworkRocketEntity constructor, update firework lifetime/power
        getHandle().lifetime = 10 * (1 + meta.getPower()) + random.nextInt(6) + random.nextInt(7);

        getHandle().getEntityData().markDirty(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
    }

    @Override
    public boolean setAttachedTo(LivingEntity entity) {
        if (isDetonated()) {
            return false;
        }

        getHandle().attachedToEntity = (entity != null) ? ((CraftLivingEntity) entity).getHandle() : null;
        return true;
    }

    @Override
    public LivingEntity getAttachedTo() {
        net.minecraft.world.entity.LivingEntity entity = getHandle().attachedToEntity;
        return (entity != null) ? (LivingEntity) entity.getBukkitEntity() : null;
    }

    @Override
    public boolean setLife(int ticks) {
        Preconditions.checkArgument(ticks >= 0, "ticks must be greater than or equal to 0");

        if (isDetonated()) {
            return false;
        }

        getHandle().life = ticks;
        return true;
    }

    @Override
    public int getLife() {
        return getHandle().life;
    }

    @Override
    public boolean setMaxLife(int ticks) {
        Preconditions.checkArgument(ticks > 0, "ticks must be greater than 0");

        if (isDetonated()) {
            return false;
        }

        getHandle().lifetime = ticks;
        return true;
    }

    @Override
    public int getMaxLife() {
        return getHandle().lifetime;
    }

    @Override
    public void detonate() {
        this.setLife(getMaxLife() + 1);
    }

    @Override
    public boolean isDetonated() {
        return getHandle().life > getHandle().lifetime;
    }

    @Override
    public boolean isShotAtAngle() {
        return getHandle().isShotAtAngle();
    }

    @Override
    public void setShotAtAngle(boolean shotAtAngle) {
        getHandle().getEntityData().set(FireworkRocketEntity.DATA_SHOT_AT_ANGLE, shotAtAngle);
    }
}
