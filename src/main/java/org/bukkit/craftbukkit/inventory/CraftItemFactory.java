package org.bukkit.craftbukkit.inventory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftItemFactory implements ItemFactory {
    static final Color DEFAULT_LEATHER_COLOR = Color.fromRGB(0xA06540);
    private static final CraftItemFactory instance;

    static {
        instance = new CraftItemFactory();
        ConfigurationSerialization.registerClass(CraftMetaItem.SerializableMeta.class);
    }

    private CraftItemFactory() {
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        return isApplicable(meta, itemstack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material type) {
        type = CraftLegacy.fromLegacy(type); // This may be called from legacy item stacks, try to get the right material
        if (type == null || meta == null) {
            return false;
        }
        if (!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + meta.getClass().toString() + " not created by " + CraftItemFactory.class.getName());
        }

        return ((CraftMetaItem) meta).applicableTo(type);
    }

    @Override
    public ItemMeta getItemMeta(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return getItemMeta(material, null);
    }

    private ItemMeta getItemMeta(Material material, CraftMetaItem meta) {
        material = CraftLegacy.fromLegacy(material); // This may be called from legacy item stacks, try to get the right material
        switch (material) {
        case AIR:
            return null;
        case WRITTEN_BOOK:
            return meta instanceof CraftMetaBookSigned ? meta : new CraftMetaBookSigned(meta);
        case WRITABLE_BOOK:
            return meta != null && meta.getClass().equals(CraftMetaBook.class) ? meta : new CraftMetaBook(meta);
        case CREEPER_HEAD:
        case CREEPER_WALL_HEAD:
        case DRAGON_HEAD:
        case DRAGON_WALL_HEAD:
        case PIGLIN_HEAD:
        case PIGLIN_WALL_HEAD:
        case PLAYER_HEAD:
        case PLAYER_WALL_HEAD:
        case SKELETON_SKULL:
        case SKELETON_WALL_SKULL:
        case WITHER_SKELETON_SKULL:
        case WITHER_SKELETON_WALL_SKULL:
        case ZOMBIE_HEAD:
        case ZOMBIE_WALL_HEAD:
            return meta instanceof CraftMetaSkull ? meta : new CraftMetaSkull(meta);
        case CHAINMAIL_HELMET:
        case CHAINMAIL_CHESTPLATE:
        case CHAINMAIL_LEGGINGS:
        case CHAINMAIL_BOOTS:
        case DIAMOND_HELMET:
        case DIAMOND_CHESTPLATE:
        case DIAMOND_LEGGINGS:
        case DIAMOND_BOOTS:
        case GOLDEN_HELMET:
        case GOLDEN_CHESTPLATE:
        case GOLDEN_LEGGINGS:
        case GOLDEN_BOOTS:
        case IRON_HELMET:
        case IRON_CHESTPLATE:
        case IRON_LEGGINGS:
        case IRON_BOOTS:
        case NETHERITE_HELMET:
        case NETHERITE_CHESTPLATE:
        case NETHERITE_LEGGINGS:
        case NETHERITE_BOOTS:
        case TURTLE_HELMET:
            return meta != null && meta.getClass().equals(CraftMetaArmor.class) ? meta : new CraftMetaArmor(meta);
        case LEATHER_HELMET:
        case LEATHER_CHESTPLATE:
        case LEATHER_LEGGINGS:
        case LEATHER_BOOTS:
            return meta instanceof CraftMetaColorableArmor ? meta : new CraftMetaColorableArmor(meta);
        case LEATHER_HORSE_ARMOR:
            return meta instanceof CraftMetaLeatherArmor ? meta : new CraftMetaLeatherArmor(meta);
        case POTION:
        case SPLASH_POTION:
        case LINGERING_POTION:
        case TIPPED_ARROW:
            return meta instanceof CraftMetaPotion ? meta : new CraftMetaPotion(meta);
        case FILLED_MAP:
            return meta instanceof CraftMetaMap ? meta : new CraftMetaMap(meta);
        case FIREWORK_ROCKET:
            return meta instanceof CraftMetaFirework ? meta : new CraftMetaFirework(meta);
        case FIREWORK_STAR:
            return meta instanceof CraftMetaCharge ? meta : new CraftMetaCharge(meta);
        case ENCHANTED_BOOK:
            return meta instanceof CraftMetaEnchantedBook ? meta : new CraftMetaEnchantedBook(meta);
        case BLACK_BANNER:
        case BLACK_WALL_BANNER:
        case BLUE_BANNER:
        case BLUE_WALL_BANNER:
        case BROWN_BANNER:
        case BROWN_WALL_BANNER:
        case CYAN_BANNER:
        case CYAN_WALL_BANNER:
        case GRAY_BANNER:
        case GRAY_WALL_BANNER:
        case GREEN_BANNER:
        case GREEN_WALL_BANNER:
        case LIGHT_BLUE_BANNER:
        case LIGHT_BLUE_WALL_BANNER:
        case LIGHT_GRAY_BANNER:
        case LIGHT_GRAY_WALL_BANNER:
        case LIME_BANNER:
        case LIME_WALL_BANNER:
        case MAGENTA_BANNER:
        case MAGENTA_WALL_BANNER:
        case ORANGE_BANNER:
        case ORANGE_WALL_BANNER:
        case PINK_BANNER:
        case PINK_WALL_BANNER:
        case PURPLE_BANNER:
        case PURPLE_WALL_BANNER:
        case RED_BANNER:
        case RED_WALL_BANNER:
        case WHITE_BANNER:
        case WHITE_WALL_BANNER:
        case YELLOW_BANNER:
        case YELLOW_WALL_BANNER:
            return meta instanceof CraftMetaBanner ? meta : new CraftMetaBanner(meta);
        case ALLAY_SPAWN_EGG:
        case AXOLOTL_SPAWN_EGG:
        case BAT_SPAWN_EGG:
        case BEE_SPAWN_EGG:
        case BLAZE_SPAWN_EGG:
        case CAT_SPAWN_EGG:
        case CAMEL_SPAWN_EGG:
        case CAVE_SPIDER_SPAWN_EGG:
        case CHICKEN_SPAWN_EGG:
        case COD_SPAWN_EGG:
        case COW_SPAWN_EGG:
        case CREEPER_SPAWN_EGG:
        case DOLPHIN_SPAWN_EGG:
        case DONKEY_SPAWN_EGG:
        case DROWNED_SPAWN_EGG:
        case ELDER_GUARDIAN_SPAWN_EGG:
        case ENDER_DRAGON_SPAWN_EGG:
        case ENDERMAN_SPAWN_EGG:
        case ENDERMITE_SPAWN_EGG:
        case EVOKER_SPAWN_EGG:
        case FOX_SPAWN_EGG:
        case FROG_SPAWN_EGG:
        case GHAST_SPAWN_EGG:
        case GLOW_SQUID_SPAWN_EGG:
        case GOAT_SPAWN_EGG:
        case GUARDIAN_SPAWN_EGG:
        case HOGLIN_SPAWN_EGG:
        case HORSE_SPAWN_EGG:
        case HUSK_SPAWN_EGG:
        case IRON_GOLEM_SPAWN_EGG:
        case LLAMA_SPAWN_EGG:
        case MAGMA_CUBE_SPAWN_EGG:
        case MOOSHROOM_SPAWN_EGG:
        case MULE_SPAWN_EGG:
        case OCELOT_SPAWN_EGG:
        case PANDA_SPAWN_EGG:
        case PARROT_SPAWN_EGG:
        case PHANTOM_SPAWN_EGG:
        case PIGLIN_BRUTE_SPAWN_EGG:
        case PIGLIN_SPAWN_EGG:
        case PIG_SPAWN_EGG:
        case PILLAGER_SPAWN_EGG:
        case POLAR_BEAR_SPAWN_EGG:
        case PUFFERFISH_SPAWN_EGG:
        case RABBIT_SPAWN_EGG:
        case RAVAGER_SPAWN_EGG:
        case SALMON_SPAWN_EGG:
        case SHEEP_SPAWN_EGG:
        case SHULKER_SPAWN_EGG:
        case SILVERFISH_SPAWN_EGG:
        case SKELETON_HORSE_SPAWN_EGG:
        case SKELETON_SPAWN_EGG:
        case SLIME_SPAWN_EGG:
        case SNIFFER_SPAWN_EGG:
        case SNOW_GOLEM_SPAWN_EGG:
        case SPIDER_SPAWN_EGG:
        case SQUID_SPAWN_EGG:
        case STRAY_SPAWN_EGG:
        case STRIDER_SPAWN_EGG:
        case TADPOLE_SPAWN_EGG:
        case TRADER_LLAMA_SPAWN_EGG:
        case TROPICAL_FISH_SPAWN_EGG:
        case TURTLE_SPAWN_EGG:
        case VEX_SPAWN_EGG:
        case VILLAGER_SPAWN_EGG:
        case VINDICATOR_SPAWN_EGG:
        case WANDERING_TRADER_SPAWN_EGG:
        case WARDEN_SPAWN_EGG:
        case WITCH_SPAWN_EGG:
        case WITHER_SKELETON_SPAWN_EGG:
        case WITHER_SPAWN_EGG:
        case WOLF_SPAWN_EGG:
        case ZOGLIN_SPAWN_EGG:
        case ZOMBIE_HORSE_SPAWN_EGG:
        case ZOMBIE_SPAWN_EGG:
        case ZOMBIE_VILLAGER_SPAWN_EGG:
        case ZOMBIFIED_PIGLIN_SPAWN_EGG:
            return meta instanceof CraftMetaSpawnEgg ? meta : new CraftMetaSpawnEgg(meta);
        case ARMOR_STAND:
            return meta instanceof CraftMetaArmorStand ? meta : new CraftMetaArmorStand(meta);
        case KNOWLEDGE_BOOK:
            return meta instanceof CraftMetaKnowledgeBook ? meta : new CraftMetaKnowledgeBook(meta);
        case FURNACE:
        case CHEST:
        case TRAPPED_CHEST:
        case JUKEBOX:
        case DISPENSER:
        case DROPPER:
        case ACACIA_HANGING_SIGN:
        case ACACIA_SIGN:
        case ACACIA_WALL_HANGING_SIGN:
        case ACACIA_WALL_SIGN:
        case BAMBOO_HANGING_SIGN:
        case BAMBOO_SIGN:
        case BAMBOO_WALL_HANGING_SIGN:
        case BAMBOO_WALL_SIGN:
        case BIRCH_HANGING_SIGN:
        case BIRCH_SIGN:
        case BIRCH_WALL_HANGING_SIGN:
        case BIRCH_WALL_SIGN:
        case CHERRY_HANGING_SIGN:
        case CHERRY_SIGN:
        case CHERRY_WALL_HANGING_SIGN:
        case CHERRY_WALL_SIGN:
        case CRIMSON_HANGING_SIGN:
        case CRIMSON_SIGN:
        case CRIMSON_WALL_HANGING_SIGN:
        case CRIMSON_WALL_SIGN:
        case DARK_OAK_HANGING_SIGN:
        case DARK_OAK_SIGN:
        case DARK_OAK_WALL_HANGING_SIGN:
        case DARK_OAK_WALL_SIGN:
        case JUNGLE_HANGING_SIGN:
        case JUNGLE_SIGN:
        case JUNGLE_WALL_HANGING_SIGN:
        case JUNGLE_WALL_SIGN:
        case MANGROVE_HANGING_SIGN:
        case MANGROVE_SIGN:
        case MANGROVE_WALL_HANGING_SIGN:
        case MANGROVE_WALL_SIGN:
        case OAK_HANGING_SIGN:
        case OAK_SIGN:
        case OAK_WALL_HANGING_SIGN:
        case OAK_WALL_SIGN:
        case SPRUCE_HANGING_SIGN:
        case SPRUCE_SIGN:
        case SPRUCE_WALL_HANGING_SIGN:
        case SPRUCE_WALL_SIGN:
        case WARPED_HANGING_SIGN:
        case WARPED_SIGN:
        case WARPED_WALL_HANGING_SIGN:
        case WARPED_WALL_SIGN:
        case SPAWNER:
        case BREWING_STAND:
        case ENCHANTING_TABLE:
        case COMMAND_BLOCK:
        case REPEATING_COMMAND_BLOCK:
        case CHAIN_COMMAND_BLOCK:
        case BEACON:
        case DAYLIGHT_DETECTOR:
        case HOPPER:
        case COMPARATOR:
        case SHIELD:
        case STRUCTURE_BLOCK:
        case SHULKER_BOX:
        case WHITE_SHULKER_BOX:
        case ORANGE_SHULKER_BOX:
        case MAGENTA_SHULKER_BOX:
        case LIGHT_BLUE_SHULKER_BOX:
        case YELLOW_SHULKER_BOX:
        case LIME_SHULKER_BOX:
        case PINK_SHULKER_BOX:
        case GRAY_SHULKER_BOX:
        case LIGHT_GRAY_SHULKER_BOX:
        case CYAN_SHULKER_BOX:
        case PURPLE_SHULKER_BOX:
        case BLUE_SHULKER_BOX:
        case BROWN_SHULKER_BOX:
        case GREEN_SHULKER_BOX:
        case RED_SHULKER_BOX:
        case BLACK_SHULKER_BOX:
        case ENDER_CHEST:
        case BARREL:
        case BELL:
        case BLAST_FURNACE:
        case CAMPFIRE:
        case SOUL_CAMPFIRE:
        case JIGSAW:
        case LECTERN:
        case SMOKER:
        case BEEHIVE:
        case BEE_NEST:
        case SCULK_CATALYST:
        case SCULK_SHRIEKER:
        case SCULK_SENSOR:
        case CALIBRATED_SCULK_SENSOR:
        case CHISELED_BOOKSHELF:
        case DECORATED_POT:
        case SUSPICIOUS_SAND:
        case SUSPICIOUS_GRAVEL:
            return new CraftMetaBlockState(meta, material);
        case TROPICAL_FISH_BUCKET:
            return meta instanceof CraftMetaTropicalFishBucket ? meta : new CraftMetaTropicalFishBucket(meta);
        case AXOLOTL_BUCKET:
            return meta instanceof CraftMetaAxolotlBucket ? meta : new CraftMetaAxolotlBucket(meta);
        case CROSSBOW:
            return meta instanceof CraftMetaCrossbow ? meta : new CraftMetaCrossbow(meta);
        case SUSPICIOUS_STEW:
            return meta instanceof CraftMetaSuspiciousStew ? meta : new CraftMetaSuspiciousStew(meta);
        case COD_BUCKET:
        case PUFFERFISH_BUCKET:
        case SALMON_BUCKET:
        case ITEM_FRAME:
        case GLOW_ITEM_FRAME:
        case PAINTING:
            return meta instanceof CraftMetaEntityTag ? meta : new CraftMetaEntityTag(meta);
        case COMPASS:
            return meta instanceof CraftMetaCompass ? meta : new CraftMetaCompass(meta);
        case BUNDLE:
            return meta instanceof CraftMetaBundle ? meta : new CraftMetaBundle(meta);
        case GOAT_HORN:
            return meta instanceof CraftMetaMusicInstrument ? meta : new CraftMetaMusicInstrument(meta);
        default:
            return new CraftMetaItem(meta);
        }
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) {
        if (meta1 == meta2) {
            return true;
        }
        if (meta1 != null && !(meta1 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("First meta of " + meta1.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta2 != null && !(meta2 instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Second meta " + meta2.getClass().getName() + " does not belong to " + CraftItemFactory.class.getName());
        }
        if (meta1 == null) {
            return ((CraftMetaItem) meta2).isEmpty();
        }
        if (meta2 == null) {
            return ((CraftMetaItem) meta1).isEmpty();
        }

        return equals((CraftMetaItem) meta1, (CraftMetaItem) meta2);
    }

    boolean equals(CraftMetaItem meta1, CraftMetaItem meta2) {
        /*
         * This couldn't be done inside of the objects themselves, else force recursion.
         * This is a fairly clean way of implementing it, by dividing the methods into purposes and letting each method perform its own function.
         *
         * The common and uncommon were split, as both could have variables not applicable to the other, like a skull and book.
         * Each object needs its chance to say "hey wait a minute, we're not equal," but without the redundancy of using the 1.equals(2) && 2.equals(1) checking the 'commons' twice.
         *
         * Doing it this way fills all conditions of the .equals() method.
         */
        return meta1.equalsCommon(meta2) && meta1.notUncommon(meta2) && meta2.notUncommon(meta1);
    }

    public static CraftItemFactory instance() {
        return instance;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) {
        Validate.notNull(stack, "Stack cannot be null");
        return asMetaFor(meta, stack.getType());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) {
        Validate.notNull(material, "Material cannot be null");
        if (!(meta instanceof CraftMetaItem)) {
            throw new IllegalArgumentException("Meta of " + (meta != null ? meta.getClass().toString() : "null") + " not created by " + CraftItemFactory.class.getName());
        }
        return getItemMeta(material, (CraftMetaItem) meta);
    }

    @Override
    public Color getDefaultLeatherColor() {
        return DEFAULT_LEATHER_COLOR;
    }

    @Override
    public ItemStack createItemStack(String input) throws IllegalArgumentException {
        try {
            ItemParser.ItemResult arg = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), new StringReader(input));

            Item item = arg.item().value();
            net.minecraft.world.item.ItemStack nmsItemStack = new net.minecraft.world.item.ItemStack(item);

            CompoundTag nbt = arg.nbt();
            if (nbt != null) {
                nmsItemStack.setTag(nbt);
            }

            return CraftItemStack.asCraftMirror(nmsItemStack);
        } catch (CommandSyntaxException ex) {
            throw new IllegalArgumentException("Could not parse ItemStack: " + input, ex);
        }
    }

    @Override
    public Material updateMaterial(ItemMeta meta, Material material) throws IllegalArgumentException {
        return ((CraftMetaItem) meta).updateMaterial(material);
    }
}
