package cc.slack.features.commands.impl;

import cc.slack.Slack;
import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.api.CMDInfo;
import cc.slack.features.friends.FriendManager;
import cc.slack.utils.other.PrintUtil;

import java.util.ArrayList;

@CMDInfo(
        name = "friend",
        alias = "f",
        description = "Add or remove a friend."
)
public class friendCMD extends CMD {

    @Override
    public void onCommand(String[] args, String cmd) {
        if (args.length == 0) {
            PrintUtil.message("§cInvalid use of arguments. Format: .friend <add/remove/list> <name>");
            return;
        }

        String action = args[0].toLowerCase();
        FriendManager friendManager = Slack.getInstance().getFriendManager();

        try {
            if (action.equals("add")) {
                if (args.length != 2) {
                    PrintUtil.message("§cInvalid use of arguments. Format: .friend add <name>");
                    return;
                }
                String friend = args[1].replace('_', ' ');
                friendManager.addFriend(friend);
                PrintUtil.message("§fSuccessfully added §a" + friend + " §fas a friend.");
            } else if (action.equals("remove")) {
                if (args.length != 2) {
                    PrintUtil.message("§cInvalid use of arguments. Format: .friend remove <name>");
                    return;
                }
                String friend = args[1].replace('_', ' ');
                friendManager.removeFriend(friend);
                PrintUtil.message("§fSuccessfully removed §c" + friend + " §ffrom friends.");
            } else if (action.equals("list")) {
                if (args.length != 1) {
                    PrintUtil.message("§cInvalid use of arguments. Format: .friend list");
                    return;
                }
                listFriends(friendManager);
            } else {
                PrintUtil.message("§cInvalid action. Use 'add', 'remove', or 'list'.");
            }
        } catch (Exception e) {
            PrintUtil.message("§cAn error occurred while processing the command.");
        }
    }

    private void listFriends(FriendManager friendManager) {
        ArrayList<String> friends = friendManager.getFriends();
        if (friends.isEmpty()) {
            PrintUtil.message("§cYou have no friends added.");
        } else {
            PrintUtil.message("§fFriends list:");
            for (String friend : friends) {
                PrintUtil.message("§f- §a" + friend);
            }
        }
    }
}
