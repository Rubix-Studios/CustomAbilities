package nl.rubixstudios.partneritems.util;

import org.bukkit.ChatColor;

/**
 * @author Djorr
 * @created 07/11/2022 - 18:25
 * @project custom-enchants
 */
public class ColorUtil {

    public static String translate(String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }

    public static String strip(String line) {
        return ChatColor.stripColor(line);
    }
}
