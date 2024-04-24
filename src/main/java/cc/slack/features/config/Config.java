package cc.slack.features.config;

import cc.slack.Slack;
import cc.slack.utils.other.FileUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import cc.slack.utils.client.mc;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;
import java.util.Map;

@Getter
@Setter
public class Config extends mc {

//    private final File directory = new File(Minecraft.getMinecraft().mcDataDir, "/" + "SlackClient" + "/Configs");
//
//    private final String name;
//
//    public Config(String name) {
//        this.name = name;
//    }
//
//    public void write() {
//        JsonObject jsonObject = new JsonObject();
//
//        Slack.getInstance().getModuleManager().getModules(module -> {
//            JsonObject mObject = new JsonObject();
//
//            mObject.addProperty("state", module.isEnabled());
//            mObject.addProperty("hide", module.isVisible());
//            mObject.addProperty("bind", module.getKeyBind());
//
//            JsonObject vObject = new JsonObject();
//
//            module.ge.forEach(property -> {
//                if (property instanceof BooleanProperty)
//                    vObject.addProperty(property.getName(), ((BooleanProperty) property).isEnabled());
//
//                if (property instanceof ModeProperty)
//                    vObject.addProperty(property.getName(), ((ModeProperty) property).getMode());
//
//                if (property instanceof NumberProperty)
//                    vObject.addProperty(property.getName(), ((NumberProperty) property).getValue());
//
//                if (property instanceof ColorProperty) {
//                    vObject.addProperty(property.getName(),((ColorProperty) property).getColor().getRGB());
//                }
//
//                if (property instanceof InputProperty) {
//                    vObject.addProperty(property.getName(), ((InputProperty) property).getInput());
//                }
//            });
//
//            mObject.add("values", vObject);
//            jsonObject.add(module.getName(), mObject);
//        });
//
//        FileUtil.writeJsonToFile(jsonObject, new File(directory, name + ".json").getAbsolutePath());
//    }
//    public void read() {
//        JsonObject config = FileUtil.readJsonFromFile(
//                new File(directory, name + ".json"
//                ).getAbsolutePath());
//
//        for (Map.Entry<String, JsonElement> entry : config.entrySet()) {
//            Actinium.INSTANCE.getModuleManager().get.forEach(module -> {
//                if (entry.getKey().equalsIgnoreCase(module.getName())) {
//                    JsonObject json = (JsonObject) entry.getValue();
//
//                    module.setEnabled(json.get("state").getAsBoolean());
//                    module.setVisible(json.get("hide").getAsBoolean());
//                    module.setKeyBind(json.get("bind").getAsInt());
//
//                    JsonObject values = json.get("values").getAsJsonObject();
//                    for (Map.Entry<String, JsonElement> value : values.entrySet()) {
//                        if (module.getValueByName(value.getKey()) != null) {
//                            try {
//                                Property v = module.getValueByName(value.getKey());
//
//                                if (v instanceof BooleanProperty)
//                                    ((BooleanProperty) v).setEnabled(value.getValue().getAsBoolean());
//
//                                if (v instanceof ModeProperty)
//                                    ((ModeProperty) v).setMode(value.getValue().getAsString());
//
//                                if (v instanceof NumberProperty)
//                                    ((NumberProperty) v).setValue(value.getValue().getAsDouble());
//
//                                if (v instanceof ColorProperty) {
//                                    ((ColorProperty) v).setColor(new Color(value.getValue().getAsInt()));
//                                }
//                                if (v instanceof InputProperty) {
//                                    ((InputProperty) v).setInput(value.getValue().getAsString());
//                                    ((InputProperty) v).getImString().set(value.getValue().getAsString());
//                                }
//                            } catch (Exception e) {
//                                // Empty Catch Block
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }
}