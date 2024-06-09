package cc.slack.features.commands.impl;

import cc.slack.Slack;
import cc.slack.features.modules.api.Module;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.features.modules.api.settings.Value;
import cc.slack.features.modules.api.settings.impl.*;
import cc.slack.utils.other.PrintUtil;
import net.minecraft.util.ChatFormatting;

import java.util.Arrays;

@CMDInfo(
        name = "Set",
        alias = "s",
        description = "Sets a setting in a module to a given value."
)
public class setCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length != 3) {
            PrintUtil.message("§cInvalid use of arguments. Format: .set module_name setting_name [setting value]");
            return;
        }

        String module_name = args[0].replace('_', ' ');
        String setting_name = args[1].replace('_', ' ');
        String value = args[2].toLowerCase();

        Module module;
        Value setting;

        try {
            module = Slack.getInstance().getModuleManager().getModuleByName(module_name);
        } catch (Exception e) {
            PrintUtil.message("§cCould not find module named: " + module_name);
            return;
        }

        try {
            setting = module.getValueByName(setting_name);
        } catch (Exception e) {
            PrintUtil.message("§cCould not find setting in " + module_name + " named: " + setting_name);
            return;
        }

        if (setting instanceof BooleanValue) {
            if (value == "true" || value == "on" || value == "1" || value == "t") {
                module.getValueByName(setting_name).setValue(true);
                PrintUtil.message("§dSet " + setting_name + " to True.");
            } else if (value == "false" || value == "off" || value == "0" || value == "f") {
                module.getValueByName(setting_name).setValue(false);
                PrintUtil.message("§dSet " + setting_name + " to False.");
            } else {
                PrintUtil.message("§cCould not turn " + value + " into boolean.");
                return;
            }
        } else if (setting instanceof ModeValue) {
            if (Arrays.asList(((ModeValue<?>) setting).getModes()).contains(value)) {
                ((ModeValue<?>) setting).setValueFromString(value);
                PrintUtil.message("§dSet " + setting_name + " to " + value + ".");
            } else {
                PrintUtil.message("§dCould not set " + setting_name + " to " + value + ".");
            }
        } else if (setting instanceof StringValue) {
            setting.setValue(value);
            PrintUtil.message("§dSet " + setting_name + " to " + value + ".");
        } else if (setting instanceof NumberValue) {
            if (((NumberValue<?>) setting).getMaximum() instanceof Double || ((NumberValue<?>) setting).getMaximum() instanceof Float) {
                try {
                    if (((NumberValue<?>) setting).getMaximum() instanceof Double) {
                        setting.setValue(Double.valueOf(value));
                    } else {
                        setting.setValue(Float.valueOf(value));
                    }
                } catch (Exception e) {
                    PrintUtil.message("§dCould not set " + value + " to decimal.");
                }
            } else {
                try {
                    if (((NumberValue<?>) setting).getMinimum() instanceof Integer) {
                        setting.setValue(Integer.valueOf(value));
                    } else {
                        setting.setValue(Long.valueOf(value));
                    }
                } catch (Exception e) {
                    PrintUtil.message("§dCould not set " + value + " to integer.");
                }
            }
        } else {
            PrintUtil.message("§dCould not set " + setting_name + " to " + value + ".");
        }
    }
}
