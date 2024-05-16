package cc.slack.features.commands.impl;

import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.features.config.ConfigManager;
import cc.slack.utils.other.PrintUtil;

@CMDInfo(
        name = "Config",
        alias = "c",
        description = "Save and Load Configs."
)
public class ConfigCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        switch (args.length) {
            case 0:
                configsMessage();
                commandsMessage();
                break;
            case 1:
                switch (args[0]) {
                    case "save":
                        ConfigManager.saveConfig(ConfigManager.currentConfig);
                    case "load":
                    case "delete":
                        commandsMessage();
                        break;
                    case "list":
                        configsMessage();
                        break;
                }
                break;
            case 2:
                switch (args[0]) {
                    case "save":
                        ConfigManager.saveConfig(args[1]);
                    case "load":
                        ConfigManager.loadConfig(args[1]);
                    case "delete":
                        ConfigManager.delete(args[1]);
                    case "list":
                        configsMessage();
                        break;
                }
                break;
            default:
                switch (args[0]) {
                    case "save":
                        ConfigManager.saveConfig(args.toString().substring(4).replace(' ', '_'));
                    case "load":
                        ConfigManager.loadConfig(args.toString().substring(4).replace(' ', '_'));
                    case "delete":
                        ConfigManager.delete(args.toString().substring(4).replace(' ', '_'));
                    case "list":
                        configsMessage();
                        break;
                }
                break;

        }
    }

    private void configsMessage() {
        PrintUtil.message("§b§lSlack configs:");
        for (String str : ConfigManager.getConfigList()) {
            PrintUtil.message("§3 " + str);
        }
    }

    private void commandsMessage() {
        PrintUtil.message("§c§lConfig commands:");
        PrintUtil.message("§4§o .config save [config name]");
        PrintUtil.message("§4§o .config load [config name]");
        PrintUtil.message("§4§o .config delete [config name]");
        PrintUtil.message("§4§o .config list");
    }

}
