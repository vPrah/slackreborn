// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.slack.Slack;
import cc.slack.features.modules.api.settings.Value;
import cc.slack.utils.EventUtil;
import cc.slack.utils.drag.DragUtil;
import lombok.Getter;
import lombok.Setter;

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
    public String getMode() { return ""; }
	public void setXYPosition(double x, double y) { }

    public void toggle() {
        setToggle(!toggle);
    }

    public void enableModule() {
        if(!toggle)
            toggle();
    }
    public void disableModule() {
        if(toggle)
            toggle();
    }

    public void setToggle(boolean toggle) {
        if (this.toggle == toggle) return;

        this.toggle = toggle;

        if (toggle) {
            EventUtil.register(this);
            onEnable();
            Slack.getInstance().addNotification("Enabled module: " + name + ".", " ", 2000L, Slack.NotificationStyle.SUCCESS);
        } else {
            EventUtil.unRegister(this);
            onDisable();
            Slack.getInstance().addNotification("Disabled module: " + name + ".", " ", 2000L, Slack.NotificationStyle.FAIL);
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
    
	public DragUtil getPosition(){
		return null;
	}

    public void addSettings(Value... settings) {
        setting.addAll(Arrays.asList(settings));
    }
}
