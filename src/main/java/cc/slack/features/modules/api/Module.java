// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.api;

import cc.slack.Slack;
import cc.slack.features.modules.api.settings.Value;
import cc.slack.utils.EventUtil;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public abstract class Module {

    private final List<Value> setting = new ArrayList<>();

    final ModuleInfo moduleInfo = getClass().getAnnotation(ModuleInfo.class);
    private final String name = moduleInfo.name();
    private final String displayName = moduleInfo.displayName().isEmpty() ? moduleInfo.name() : moduleInfo.displayName();
    private final Category category = moduleInfo.category();

    private int key = moduleInfo.key();
    private boolean toggle;

    public void onToggled() {}
    public void onEnable() {}
    public void onDisable() {}

    public void toggle() {
        setToggle(!toggle);
    }

    public void setToggle(boolean toggle) {
        if (this.toggle == toggle) return;

        this.toggle = toggle;

        if (toggle) {
            EventUtil.register(this);
            onEnable();
            Slack.getInstance().addNotification("Enabled module: " + name + ".", " ", 2000L);
        } else {
            EventUtil.unRegister(this);
            onDisable();
            Slack.getInstance().addNotification("Disabled module: " + name + ".", " ", 2000L);
        }
        onToggled();
    }

    public Value getValueByName(String n) {
        for (Value m : setting) {
            if (m.getName().equals(n))
                return m;
        }

        return null;
    }

    public void addSettings(Value... settings) {
        setting.addAll(Arrays.asList(settings));
    }
}
