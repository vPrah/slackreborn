package cc.zenith.features.commands;

import cc.zenith.features.commands.api.CMD;
import cc.zenith.features.commands.impl.HelpCMD;
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
