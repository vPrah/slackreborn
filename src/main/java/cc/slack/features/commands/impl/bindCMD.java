package cc.slack.features.commands.impl;

import cc.slack.Slack;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.utils.other.PrintUtil;
import cc.slack.features.modules.api.Module;
import net.minecraft.util.ChatFormatting;
import org.lwjgl.input.Keyboard;

@CMDInfo(
        name = "Bind",
        alias = "b",
        description = "Binds a module to a key."
)
public class bindCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            PrintUtil.message("§cUsage: .bind [module] [key] or .bind list");
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                bindMessage();
            } else {
                PrintUtil.message("§cUsage: .bind [module] [key] or .bind list");

            }
        }

        if (args.length == 2) {
            String module_name = args[0].replace('_', ' ');
            Module module;
            try {
                module = Slack.getInstance().getModuleManager().getModuleByName(module_name);
            } catch (Exception e) {
                PrintUtil.message("§cCould not find module named: " + module_name);
                return;
            }

            try {
                module.setKey(Keyboard.getKeyIndex(args[1]));
                PrintUtil.message("§f Bound §c" + module_name + "§f to §c" + args[1] + "§f.");
            } catch (Exception e) {
                PrintUtil.message("§cCould not find key named: " + args[1]);
                return;
            }
        }

        if (args.length > 2) {
            PrintUtil.message("§cUsage: .bind [module] [key] or .bind list");
            PrintUtil.message("§cPlease replace spaces with underscores in the module name.");
        }

    }

    private void bindMessage() {
        PrintUtil.message("§f§lModule binds:");
        for (Module m : Slack.getInstance().getModuleManager().getModules()) {
            if (m.getKey() != 0) {
                PrintUtil.message("§f §l" + m.getName() + "§l [§c" + Keyboard.getKeyName(m.getKey()) + "§f]");
            }
        }
    }

}
