package nl.rubixstudios.partneritems.util.item;

import nl.rubixstudios.partneritems.data.Config;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemUtil {

    public static final List<int[]> BLOCK_RELATIVES;

    public static Enchantment FAKE_GLOW;


    public static void removeOneItem(Player player) {
        if(player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            return;
        }

        player.getInventory().setItemInHand(new ItemStack(Material.AIR));
    }

    public static ItemMeta getNewItemUsagesMeta(List<String> language, ItemStack itemStack, int currentUsages, int maxUsages) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>();

        language.forEach(line -> lore.add(line
                .replace("<currentUsages>", "" + currentUsages)
                .replace("<maxUsages>", "" + maxUsages)));

        itemMeta.setLore(lore);
        return itemMeta;
    }


    static {
        BLOCK_RELATIVES = new ArrayList<>();

        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                for(int z = -1; z <= 1; z++) {
                    BLOCK_RELATIVES.add(new int[] { x, y, z });
                }
            }
        }

        FAKE_GLOW = new FakeGlow(70);
    }
}