package nl.rubixstudios.partneritems.data;

import lombok.Getter;
import nl.rubixstudios.partneritems.PartnerItems;
import nl.rubixstudios.partneritems.util.ColorUtil;
import nl.rubixstudios.partneritems.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Djorr
 * @created 23/11/2022 - 10:06
 * @project PartnerItems
 */
public class ConfigFile extends YamlConfiguration {

    private static PartnerItems mainInstance = PartnerItems.getInstance();

    @Getter
    private final File file;

    public ConfigFile(String name) throws RuntimeException {
        this.file = new File(mainInstance.getDataFolder(), name);

        if(!this.file.exists()) {
            mainInstance.saveResource(name, false);
        }

        try {
            this.load(this.file);
        } catch(IOException | InvalidConfigurationException e) {
            mainInstance.log("");
            mainInstance.log("&9===&b=============================================&9===");
            mainInstance.log(StringUtil.center("&cError occurred while loading " + name + ".", 51));
            mainInstance.log("");

            Stream.of(e.getMessage().split("\n")).forEach(line -> mainInstance.log(line));

            mainInstance.log("&9===&b=============================================&9===");
            throw new RuntimeException();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection getSection(String name) {
        return super.getConfigurationSection(name);
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    @Override
    public String getString(String path) {
        return ColorUtil.translate(super.getString(path, ""));
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(ColorUtil::translate).collect(Collectors.toList());
    }
}