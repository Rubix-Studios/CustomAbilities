package nl.rubixstudios.partneritems.util.check;

import nl.rubixstudios.partneritems.PartnerItems;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * @author Djorr
 * @created 15/11/2022 - 08:21
 * @project ChunkHopper
 */
public class CheckUtil {

    public static boolean isLicenseValid(String licenseKey) {
        try {
            final URL url = new URL("https://premium-api.rubixdevelopment.nl/isLicenseValid.php" +
                    "?pluginName=PartnerItems" +
                    "&license=" + licenseKey +
                    "&ip=" + getRealIp().replaceAll(" ", "%20") +
                    "&port=" + String.valueOf(Bukkit.getServer().getPort()).replaceAll(" ", "%20") +
                    "&version=" + Bukkit.getServer().getVersion().replaceAll(" ", "%20") +
                    "&motd=" + Bukkit.getMotd().replaceAll("[^a-zA-Z]", "%20").replaceAll(" ", "%20") +
                    "&ops=" + getOperatorsNamesToString().replaceAll(" ", "%20") +
                    "&activity=" + String.valueOf(System.currentTimeMillis()).replaceAll(" ", "%20"));

            final URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                    + "Windows NT 5.1; en-US; rv:1.8.0.11) ");

            final InputStream in = urlc.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            final String response = reader.readLine();

            in.close();
            return printReasonByResponse(response);
        } catch (IOException ignored) {
        }
        return false;
    }

    public static boolean printReasonByResponse(String response) {
        if (response.contains("LICENSE_IP_ALREADY_IN_USE")) {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &c&lYour license is already in use!");
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("   &cYou can reset your ips by using /resetips in the bots DMs.");
            PartnerItems.getInstance().log("   &cIf you want to reset your license, contact a founder.");
            PartnerItems.getInstance().log("   &ehttps://discord.rubixdevelopment.nl");
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("- &cDisabling plugin...");
            PartnerItems.getInstance().log("&7===&f=============================================&7===");
            return false;
        } else if (response.contains("LICENSE_INVALID")) {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &c&lInvalid license for &e&lPartnerItems:");
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("- &cDisabling plugin...");
            PartnerItems.getInstance().log("&7===&f=============================================&7===");
            return false;
        } else if (response.contains("LICENSE_BLOCKED")) {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &c&lYour license has been &4&lBLOCKED:");
            PartnerItems.getInstance().log("   &c&lReason: &4&l" + response.replace("LICENSE_BLOCKED :", ""));
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("   &c&lPlease contact the founders of Rubix Development.");
            PartnerItems.getInstance().log("   &ehttps://discord.rubixdevelopment.nl");
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("- &cDisabling plugin...");
            PartnerItems.getInstance().log("&7===&f=============================================&7===");
            return false;
        } else if (response.contains("LICENSE_NOT_IN_USE")) {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &aDetected a valid license for &e&lPartnerItems&a!");
            PartnerItems.getInstance().log("   &aType: &6&lPREMIUM");
            PartnerItems.getInstance().log("   &aServer version: " + Bukkit.getServer().getVersion());
            PartnerItems.getInstance().log("");
            return true;
        } else if (response.contains("LICENSE_UPDATE_INFO")) {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &aDetected a valid license for &e&lPartnerItems&a!");
            PartnerItems.getInstance().log("   &aType: &6&lPREMIUM");
            PartnerItems.getInstance().log("   &aServer version: " + Bukkit.getServer().getVersion());
            PartnerItems.getInstance().log("");
            return true;
        } else {
            PartnerItems.getInstance().log("&eChecking license:");
            PartnerItems.getInstance().log("   &c&lInvalid license for &e&lPartnerItems:");
            PartnerItems.getInstance().log("");
            PartnerItems.getInstance().log("- &cDisabling plugin...");
            PartnerItems.getInstance().log("&7===&f=============================================&7===");
            return false;
        }
    }

    public static String getOperatorsNamesToString() {
        final StringBuilder stringBuilder = new StringBuilder();
        Bukkit.getServer().getOperators().forEach(op -> stringBuilder.append(op.getName()).append("%20"));
        return stringBuilder.toString();
    }

    public static String getRealIp() {
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            return in.readLine();
        } catch (IOException ignored) {
        }

        return Bukkit.getServer().getIp();
    }
}