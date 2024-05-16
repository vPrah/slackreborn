package cc.slack.features.config;

import cc.slack.utils.other.PrintUtil;
import lombok.Getter;
import lombok.Setter;
import cc.slack.utils.client.mc;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class ConfigManager extends mc {
/*
    public static final Map<String, Config> configs = new HashMap<>();

    private static final File configFolder = new File(Minecraft.getMinecraft().mcDataDir, "/" + "SlackClient" + "/Configs");

    private Config activeConfig;

    private static void refresh() {
        for (File file : Objects.requireNonNull(configFolder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                String name = file.getName().replaceAll(".json", "");
                Config config = new Config(name);
                configs.put(config.getName(), config);
            }
        }
    }

    public void init() {
        configFolder.mkdirs();

        refresh();

        if (getConfig("default") == null) {
            Config config = new Config("default");
            config.write();
            configs.put(config.getName(), config);
        } else getConfig("default").read();
    }

    public void stop() {
        if (getConfig("default") == null) {
            Config config = new Config("default");
            config.write();
        } else getConfig("default").write();
    }

    public static Config getConfig(String name) {
        return configs.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst().map(configs::get).orElse(null);
    }

    public static void saveConfig(String configName){
        if (getConfig(configName) == null) {
            Config config = new Config(configName);
            config.write();
        } else getConfig(configName).write();

        PrintUtil.print("Saved config " + configName + ".");
    }

    public boolean delete(String configName) {
        Config existingConfig = getConfig(configName);

        if (existingConfig != null) {
            File configFile = new File(existingConfig.getDirectory().toString());

            if (configFile.exists()) {
                boolean deleted = configFile.delete();
                if (!deleted) {
                    System.err.println("Error: Unable to delete the config file");
                    return false;
                }
            }
        }
        return true;
    }

    public static void loadConfig(String configName) {
        refresh();

        if (getConfig(configName) != null) {
            PrintUtil.print("Loaded config " + configName + ".");
            getConfig(configName).read();
        } else {
            PrintUtil.print("Failed to load config.");
        }
    }
*/
}
