package cc.zenith.utils.other;

import cc.zenith.utils.client.MC;
import net.minecraft.util.ChatComponentText;

public final class PrintUtil extends MC {

    public static void print(String message) {
        System.out.println("[Zenith] " + message);
    }

    public static void debugMessage(String message) {
        getPlayer().addChatMessage(new ChatComponentText("§f[§cDEBUG§f] §e" + message));
    }

    public static void message(String message) {
        getPlayer().addChatMessage(new ChatComponentText("§cZenith §e» §f" + message));
    }
}
