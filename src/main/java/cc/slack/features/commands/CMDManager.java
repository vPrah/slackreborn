package cc.slack.features.commands;

import cc.slack.features.commands.api.CMD;
import cc.slack.features.commands.impl.ConfigCMD;
import cc.slack.features.commands.impl.HelpCMD;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class CMDManager {

    private final List<CMD> commands = new ArrayList<>();
    private final String prefix = ".";

    public void initialize() {
        try {
            addCommands(
                    new ConfigCMD(),
                    new HelpCMD()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCommands(CMD... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }
}
