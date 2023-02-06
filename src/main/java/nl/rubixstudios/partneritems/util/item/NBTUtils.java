package nl.rubixstudios.partneritems.util.item;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Spraxs
 * Date: 24-7-2019
 */

public class NBTUtils {

    /*
     * * NBT Tag Data * *
     */
    public static boolean hasItemData(ItemStack itemStack, String key) {
        if (itemStack.getItemMeta() == null) return false;

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) return false;


        return itemCompound.hasKey(key);
    }

    /*
     *   *   Integer *   *
     */
    public static void setItemDataInt(ItemStack itemStack, String key, int value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagInt(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
////            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static int getItemDataInt(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getInt(key);
    }

    /*
     *   *   Integer Array *   *
     */

    public static void setItemDataIntArray(ItemStack itemStack, String key, int[] value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagIntArray(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static int[] getItemDataIntArray(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getIntArray(key);
    }

    /*
     *   *   Byte *   *
     */

    public static void setItemDataByte(ItemStack itemStack, String key, byte value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagByte(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static int getItemDataByte(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getByte(key);
    }

    /*
     *   *   Float *   *
     */

    public static void setItemDataFloat(ItemStack itemStack, String key, float value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagFloat(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static float getItemDataFloat(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getFloat(key);
    }

    /*
     *   *   Double *   *
     */

    public static void setItemDataDouble(ItemStack itemStack, String key, double value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagDouble(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static double getItemDataDouble(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getDouble(key);
    }

    /*
     *   *   Long *   *
     */

    public static void setItemDataLong(ItemStack itemStack, String key, long value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagLong(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static long getItemDataLong(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getLong(key);
    }

    /*
     *   *   String *   *
     */

    public static void setItemDataString(ItemStack itemStack, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagString(value));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static String getItemDataString(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return itemCompound.getString(key);
    }

    /*
     *   *   Boolean *   *
     */

    public static void setItemDataBoolean(ItemStack itemStack, String key, boolean value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        itemCompound.set(key, new NBTTagByte(value ? (byte) 1 : (byte) 0));

        nmsItem.setTag(itemCompound);

        if (!hasItemMeta(nmsItem)) {
//            Bukkit.broadcastMessage("No item meta");

            return;
        }

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static boolean getItemDataBoolean(ItemStack itemStack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : null;

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }

        return (itemCompound.getByte(key) == (byte) 1);
    }

    static boolean hasItemMeta(net.minecraft.server.v1_8_R3.ItemStack item) {
        return item != null && item.getTag() != null && !item.getTag().isEmpty();
    }

    public static void clearItemTags(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("No item meta");

        nmsItem.setTag(new NBTTagCompound());

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    public static void clearAttributeModifiers(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("No item meta");

        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (itemCompound == null) {
            throw new NullPointerException("Item compound was null");
        }


        itemCompound.set("AttributeModifiers", new NBTTagList());

        nmsItem.setTag(itemCompound);

        itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
    }

    /*
        Flags
     */

    public static void setItemFlagsHidden(ItemStack itemStack, boolean hidden) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("No item meta");

        if (hidden) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_POTION_EFFECTS);
        } else {
            itemMeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_DESTROYS,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_PLACED_ON,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_POTION_EFFECTS);
        }

        itemStack.setItemMeta(itemMeta);
    }

    public static boolean areItemFlagsHidden(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("No item meta");

        return itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) &&
                itemMeta.hasItemFlag(ItemFlag.HIDE_DESTROYS) &&
                itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) &&
                itemMeta.hasItemFlag(ItemFlag.HIDE_PLACED_ON) &&
                itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) &&
                itemMeta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
    }
}