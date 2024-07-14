package cc.slack.features.commands.impl;

import cc.slack.Slack;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.utils.other.PrintUtil;
import net.minecraft.util.ChatFormatting;

import static cc.slack.utils.client.IMinecraft.mc;

@CMDInfo(
        name = "ign",
        alias = "IGN",
        description = "Returns your current ign."
)
public class ignCMD extends CMD {

    @Override
    public void onCommand(String[] args, String command) {
        PrintUtil.message(mc.thePlayer.getNameClear());
    }

}
