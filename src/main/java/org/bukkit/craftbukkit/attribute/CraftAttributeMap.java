package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftAttributeMap implements Attributable {

    private final AttributeMap handle;

    public CraftAttributeMap(AttributeMap handle) {
        this.handle = handle;
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        Preconditions.checkArgument(attribute != null, "attribute");
        net.minecraft.world.entity.ai.attributes.AttributeInstance nms = handle.getInstance(toMinecraft(attribute));

        return (nms == null) ? null : new CraftAttributeInstance(nms, attribute);
    }

    public static net.minecraft.world.entity.ai.attributes.Attribute toMinecraft(Attribute attribute) {
        return BuiltInRegistries.ATTRIBUTE.get(CraftNamespacedKey.toMinecraft(attribute.getKey()));
    }

    public static Attribute fromMinecraft(String nms) {
        return Registry.ATTRIBUTE.get(CraftNamespacedKey.fromString(nms));
    }
}
