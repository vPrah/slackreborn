package cc.slack.features.commands.impl;

import cc.slack.Slack;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.utils.other.PrintUtil;
import cc.slack.features.modules.api.Module;

@CMDInfo(
        name = "Toggle",
        alias = "t",
        description = "Toggles a module."
)
public class ToggleCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        String moduleName = "";
        try {
            if (args.length != 1) {
                PrintUtil.message("§cUsage: .t [module]");
                return;
            }

            moduleName = args[0];
            Module module = Slack.getInstance().getModuleManager().getModuleByName(moduleName);
            if (module == null) {
                PrintUtil.message("§cCould not find module named: " + moduleName);
                return;
            }

            module.toggle();
            PrintUtil.message("§b"+moduleName + " " + "§fenabled.");
        } catch (Exception e) {
            PrintUtil.message("§cCould not find module named: " + moduleName);
        }
    }
}