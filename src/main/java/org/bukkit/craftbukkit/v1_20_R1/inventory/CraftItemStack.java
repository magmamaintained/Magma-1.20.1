package org.bukkit.craftbukkit.v1_20_R1.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_20_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLegacy;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Map;
import java.util.Objects;

import static org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaItem.ENCHANTMENTS;

@DelegateDeserialization(ItemStack.class)
public final class CraftItemStack extends ItemStack {

    public static net.minecraft.world.item.ItemStack asNMSCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return stack.handle == null ? net.minecraft.world.item.ItemStack.EMPTY : stack.handle.copy();
        }
        if (original == null || original.getType() == Material.AIR) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        Item item = CraftMagicNumbers.getItem(original.getType(), original.getDurability());

        if (item == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, original.getAmount());
        if (original.hasItemMeta()) {
            setItemMeta(stack, original.getItemMeta());
        }
        return stack;
    }

    public static net.minecraft.world.item.ItemStack copyNMSStack(net.minecraft.world.item.ItemStack original, int amount) {
        net.minecraft.world.item.ItemStack stack = original.copy();
        stack.setCount(amount);
        return stack;
    }

    /**
     * Copies the NMS stack to return as a strictly-Bukkit stack
     */
    public static ItemStack asBukkitCopy(net.minecraft.world.item.ItemStack original) {
        if (original.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        ItemStack stack = new ItemStack(CraftMagicNumbers.getMaterial(original.getItem()), original.getCount());
        if (hasItemMeta(original)) {
            stack.setItemMeta(getItemMeta(original));
        }
        return stack;
    }

    public static CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack original) {
        return new CraftItemStack((original == null || original.isEmpty()) ? null : original);
    }

    public static CraftItemStack asCraftCopy(ItemStack original) {
        if (original instanceof CraftItemStack) {
            CraftItemStack stack = (CraftItemStack) original;
            return new CraftItemStack(stack.handle == null ? null : stack.handle.copy());
        }
        return new CraftItemStack(original);
    }

    public static CraftItemStack asNewCraftStack(Item item) {
        return asNewCraftStack(item, 1);
    }

    public static CraftItemStack asNewCraftStack(Item item, int amount) {
        return new CraftItemStack(CraftMagicNumbers.getMaterial(item), amount, (short) 0, null);
    }

    net.minecraft.world.item.ItemStack handle;

    /**
     * Mirror
     */
    private CraftItemStack(net.minecraft.world.item.ItemStack item) {
        this.handle = item;
    }

    private CraftItemStack(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability(), item.hasItemMeta() ? item.getItemMeta() : null);
    }

    private CraftItemStack(Material type, int amount, short durability, ItemMeta itemMeta) {
        setType(type);
        setAmount(amount);
        setDurability(durability);
        setItemMeta(itemMeta);
    }

    @Override
    public MaterialData getData() {
        return handle != null ? CraftMagicNumbers.getMaterialData(handle.getItem()) : super.getData();
    }

    @Override
    public Material getType() {
        return handle != null ? CraftMagicNumbers.getMaterial(handle.getItem()) : Material.AIR;
    }

    @Override
    public void setType(Material type) {
        if (getType() == type) {
            return;
        } else if (type == Material.AIR) {
            handle = null;
        } else if (CraftMagicNumbers.getItem(type) == null) { // :(
            handle = null;
        } else if (handle == null) {
            handle = new net.minecraft.world.item.ItemStack(CraftMagicNumbers.getItem(type), 1);
        } else {
            handle.setItem(CraftMagicNumbers.getItem(type));
            if (hasItemMeta()) {
                // This will create the appropriate item meta, which will contain all the data we intend to keep
                setItemMeta(handle, getItemMeta(handle));
            }
        }
        setData(null);
    }

    @Override
    public int getAmount() {
        return handle != null ? handle.getCount() : 0;
    }

    @Override
    public void setAmount(int amount) {
        if (handle == null) {
            return;
        }

        handle.setCount(amount);
        if (amount == 0) {
            handle = null;
        }
    }

    @Override
    public void setDurability(final short durability) {
        // Ignore damage if item is null
        if (handle != null) {
            handle.setDamageValue(durability);
        }
    }

    @Override
    public short getDurability() {
        if (handle != null) {
            return (short) handle.getDamageValue();
        } else {
            return -1;
        }
    }

    @Override
    public int getMaxStackSize() {
        return (handle == null) ? Material.AIR.getMaxStackSize() : handle.getItem().getMaxStackSize();
    }

    @Override
    public void addUnsafeEnchantment(Enchantment ench, int level) {
        Validate.notNull(ench, "Cannot add null enchantment");

        if (!makeTag(handle)) {
            return;
        }
        ListTag list = getEnchantmentList(handle);
        if (list == null) {
            list = new ListTag();
            handle.getTag().put(CraftMetaItem.ENCHANTMENTS.NBT, list);
        }
        int size = list.size();

        for (int i = 0; i < size; i++) {
            CompoundTag tag = (CompoundTag) list.get(i);
            String id = tag.getString(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            if (ench.getKey().equals(NamespacedKey.fromString(id))) {
                tag.putShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT, (short) level);
                return;
            }
        }
        CompoundTag tag = new CompoundTag();
        tag.putString(CraftMetaItem.ENCHANTMENTS_ID.NBT, ench.getKey().toString());
        tag.putShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT, (short) level);
        list.add(tag);
    }

    static boolean makeTag(net.minecraft.world.item.ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getTag() == null) {
            item.setTag(new CompoundTag());
        }

        return true;
    }

    @Override
    public boolean containsEnchantment(Enchantment ench) {
        return getEnchantmentLevel(ench) > 0;
    }

    @Override
    public int getEnchantmentLevel(Enchantment ench) {
        Validate.notNull(ench, "Cannot find null enchantment");
        if (handle == null) {
            return 0;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.getRaw(ench), handle);
    }

    @Override
    public int removeEnchantment(Enchantment ench) {
        Validate.notNull(ench, "Cannot remove null enchantment");

        ListTag list = getEnchantmentList(handle), listCopy;
        if (list == null) {
            return 0;
        }
        int index = Integer.MIN_VALUE;
        int level = Integer.MIN_VALUE;
        int size = list.size();

        for (int i = 0; i < size; i++) {
            CompoundTag enchantment = (CompoundTag) list.get(i);
            String id = enchantment.getString(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            if (ench.getKey().equals(NamespacedKey.fromString(id))) {
                index = i;
                level = 0xffff & enchantment.getShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT);
                break;
            }
        }

        if (index == Integer.MIN_VALUE) {
            return 0;
        }
        if (size == 1) {
            handle.getTag().remove(ENCHANTMENTS.NBT);
            if (handle.getTag().isEmpty()) {
                handle.setTag(null);
            }
            return level;
        }

        // This is workaround for not having an index removal
        listCopy = new ListTag();
        for (int i = 0; i < size; i++) {
            if (i != index) {
                listCopy.add(list.get(i));
            }
        }
        handle.getTag().put(CraftMetaItem.ENCHANTMENTS.NBT, listCopy);

        return level;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return getEnchantments(handle);
    }

    static Map<Enchantment, Integer> getEnchantments(net.minecraft.world.item.ItemStack item) {
        ListTag list = (item != null && item.isEnchanted()) ? item.getEnchantmentTags() : null;

        if (list == null || list.size() == 0) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<Enchantment, Integer> result = ImmutableMap.builder();

        for (int i = 0; i < list.size(); i++) {
            String id = ((CompoundTag) list.get(i)).getString(CraftMetaItem.ENCHANTMENTS_ID.NBT);
            int level = 0xffff & ((CompoundTag) list.get(i)).getShort(CraftMetaItem.ENCHANTMENTS_LVL.NBT);

            Enchantment enchant = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(id));
            if (enchant != null) {
                result.put(enchant, level);
            }
        }

        return result.build();
    }

    static ListTag getEnchantmentList(net.minecraft.world.item.ItemStack item) {
        return (item != null && item.isEnchanted()) ? item.getEnchantmentTags() : null;
    }

    @Override
    public CraftItemStack clone() {
        CraftItemStack itemStack = (CraftItemStack) super.clone();
        if (this.handle != null) {
            itemStack.handle = this.handle.copy();
        }
        return itemStack;
    }

    @Override
    public ItemMeta getItemMeta() {
        return getItemMeta(handle);
    }

    public static ItemMeta getItemMeta(net.minecraft.world.item.ItemStack item) {
        //Magma start
        if (item == null || item == net.minecraft.world.item.ItemStack.EMPTY)
            return null;

        if (item.getTag() == null) {
            ItemMeta meta = CraftItemFactory.instance().getItemMeta(getType(item));
            ((CraftMetaItem) meta).setForgeCaps(item.getForgeCaps());
            return meta;
        }

        CraftMetaItem meta = switch (getType(item)) {
            case WRITTEN_BOOK -> new CraftMetaBookSigned(item.getTag());
            case WRITABLE_BOOK -> new CraftMetaBook(item.getTag());
            case ZOMBIE_WALL_HEAD -> new CraftMetaSkull(item.getTag());
            case TURTLE_HELMET -> new CraftMetaArmor(item.getTag());
            case LEATHER_BOOTS -> new CraftMetaColorableArmor(item.getTag());
            case LEATHER_HORSE_ARMOR -> new CraftMetaLeatherArmor(item.getTag());
            case TIPPED_ARROW -> new CraftMetaPotion(item.getTag());
            case FILLED_MAP -> new CraftMetaMap(item.getTag());
            case FIREWORK_ROCKET -> new CraftMetaFirework(item.getTag());
            case FIREWORK_STAR -> new CraftMetaCharge(item.getTag());
            case ENCHANTED_BOOK -> new CraftMetaEnchantedBook(item.getTag());
            case YELLOW_WALL_BANNER -> new CraftMetaBanner(item.getTag());
            case ZOMBIFIED_PIGLIN_SPAWN_EGG -> new CraftMetaSpawnEgg(item.getTag());
            case ARMOR_STAND -> new CraftMetaArmorStand(item.getTag());
            case KNOWLEDGE_BOOK -> new CraftMetaKnowledgeBook(item.getTag());
            case SUSPICIOUS_GRAVEL ->  new CraftMetaBlockState(item.getTag(), CraftMagicNumbers.getMaterial(item.getItem()));
            case TROPICAL_FISH_BUCKET -> new CraftMetaTropicalFishBucket(item.getTag());
            case AXOLOTL_BUCKET -> new CraftMetaAxolotlBucket(item.getTag());
            case CROSSBOW -> new CraftMetaCrossbow(item.getTag());
            case SUSPICIOUS_STEW -> new CraftMetaSuspiciousStew(item.getTag());
            case PAINTING -> new CraftMetaEntityTag(item.getTag());
            case COMPASS -> new CraftMetaCompass(item.getTag());
            case BUNDLE -> new CraftMetaBundle(item.getTag());
            case GOAT_HORN -> new CraftMetaMusicInstrument(item.getTag());
            default -> new CraftMetaItem(item.getTag());
        };

        CompoundTag tag = item.getTag();
        if (tag != null) meta.offerUnhandledTags(tag);
        meta.setForgeCaps(item.getForgeCaps());
        return meta;
        //Magma end
    }

    static Material getType(net.minecraft.world.item.ItemStack item) {
        return item == null ? Material.AIR : CraftMagicNumbers.getMaterial(item.getItem());
    }

    @Override
    public boolean setItemMeta(ItemMeta itemMeta) {
        return setItemMeta(handle, itemMeta);
    }

    public static boolean setItemMeta(net.minecraft.world.item.ItemStack item, ItemMeta itemMeta) {
        if (item == null) {
            return false;
        }
        if (CraftItemFactory.instance().equals(itemMeta, null)) {
            item.setTag(null);
            return true;
        }
        if (!CraftItemFactory.instance().isApplicable(itemMeta, getType(item))) {
            return false;
        }

        itemMeta = CraftItemFactory.instance().asMetaFor(itemMeta, getType(item));
        if (itemMeta == null) return true;

        Item oldItem = item.getItem();
        Item newItem = CraftMagicNumbers.getItem(CraftItemFactory.instance().updateMaterial(itemMeta, CraftMagicNumbers.getMaterial(oldItem)));
        if (oldItem != newItem) {
            item.setItem(newItem);
        }

        CompoundTag tag = new CompoundTag();
        item.setTag(tag);

        ((CraftMetaItem) itemMeta).applyToItem(tag);
        item.convertStack(((CraftMetaItem) itemMeta).getVersion());
        // Magma start
        CompoundTag forgeCaps = ((CraftMetaItem) itemMeta).getForgeCaps();
        if (forgeCaps != null)
            item.setForgeCaps(forgeCaps.copy());
        // Magma end
        // SpigotCraft#463 this is required now by the Vanilla client, so mimic ItemStack constructor in ensuring it
        if (item.getItem() != null && item.getItem().canBeDepleted()) {
            item.setDamageValue(item.getDamageValue());
        }

        return true;
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if (!(stack instanceof CraftItemStack)) {
            return stack.getClass() == ItemStack.class && stack.isSimilar(this);
        }

        CraftItemStack that = (CraftItemStack) stack;
        if (handle == that.handle) {
            return true;
        }
        if (handle == null || that.handle == null) {
            return false;
        }
        Material comparisonType = CraftLegacy.fromLegacy(that.getType()); // This may be called from legacy item stacks, try to get the right material
        if (!(comparisonType == getType() && getDurability() == that.getDurability())) {
            return false;
        }
        //Magma start
        return hasItemMeta()
                ? (that.hasItemMeta()
                && Objects.equals(handle.getTag(), that.handle.getTag())
                && Objects.equals(handle.getForgeCaps(), that.handle.getForgeCaps()))
                : !that.hasItemMeta();
        //Magma end
    }

    @Override
    public boolean hasItemMeta() {
        return hasItemMeta(handle) && !CraftItemFactory.instance().equals(getItemMeta(), null);
    }

    static boolean hasItemMeta(net.minecraft.world.item.ItemStack item) {
        return !(item == null || item.getTag() == null || item.getTag().isEmpty());
    }
}
