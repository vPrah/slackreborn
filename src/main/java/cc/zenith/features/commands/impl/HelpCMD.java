package cc.zenith.features.commands.impl;

import cc.zenith.Zenith;
import cc.zenith.features.commands.api.CMD;
import cc.zenith.features.commands.api.CMDInfo;
import cc.zenith.utils.other.PrintUtil;
import net.minecraft.util.ChatFormatting;

@CMDInfo(
        name = "Help",
        alias = "h",
        description = "Displays all of Zenith's commands."
)
public class HelpCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length > 0) {
            PrintUtil.message("§cInvalid use of arguments, expected none.");
            return;
        }

        PrintUtil.message("Command list: ");
        Zenith.getInstance().getCmdManager().getCommands().forEach(cmd -> PrintUtil.message(cmd.getName() + " - " + ChatFormatting.GRAY + cmd.getDescription()));
    }

}
