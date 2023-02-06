package nl.rubixstudios.partneritems.util.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(material, 1, 0);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(Material material, int amount, int durability) {
        this.itemStack = new ItemStack(material, amount, (short) durability);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int i) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, i, true);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        this.itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addAmount(int amount) {
        this.itemStack.setAmount(this.itemStack.getAmount() + amount);
        return this;
    }


    public ItemBuilder setSkullOwner(String owner) {
        try {
            ((SkullMeta) this.itemMeta).setOwner(owner);
        } catch(ClassCastException ignored) { }

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder addStoredEnchantment(Enchantment enchantment, int level) {
        try {
            ((EnchantmentStorageMeta) this.itemMeta).addStoredEnchant(enchantment, level, false);
        } catch(ClassCastException ignored) { }

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addFakeGlow() {
        this.itemMeta.addEnchant(ItemUtil.FAKE_GLOW, 2, true);
        return this;
    }

    public ItemMeta getItemMeta() {
        return this.itemMeta;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}