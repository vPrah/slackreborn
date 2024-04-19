package cc.slack.utils.other;

import cc.slack.utils.client.mc;
import net.minecraft.util.ChatComponentText;

public final class PrintUtil extends mc {

    public static void print(String message) {
        System.out.println("[Slack] " + message);
    }

    public static void debugMessage(String message) {
        getPlayer().addChatMessage(new ChatComponentText("§f[§cDEBUG§f] §e" + message));
    }

    public static void message(String message) {
        getPlayer().addChatMessage(new ChatComponentText("§cZenith §e» §f" + message));
    }
}
