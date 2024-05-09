package cc.slack.features.modules.impl.other;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "RemoveEffect",
        category = Category.OTHER
)
public class RemoveEffect extends Module {

    private final BooleanValue shouldRemoveSlowness = new BooleanValue("Slowness", false);
    private final BooleanValue shouldRemoveMiningFatigue = new BooleanValue("MiningFatigue", false);
    private final BooleanValue shouldRemoveBlindness = new BooleanValue("Blindness", false);
    private final BooleanValue shouldRemoveWeakness = new BooleanValue("Weakness", false);
    private final BooleanValue shouldRemoveWither = new BooleanValue("Wither", false);
    private final BooleanValue shouldRemovePoison = new BooleanValue("Poison", false);
    private final BooleanValue shouldRemoveWaterBreathing = new BooleanValue("WaterBreathing", false);


    public RemoveEffect() {
        addSettings(shouldRemoveSlowness, shouldRemoveMiningFatigue, shouldRemoveBlindness, shouldRemoveWeakness, shouldRemoveWither, shouldRemovePoison, shouldRemoveWaterBreathing);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate(UpdateEvent event) {
        if (mc.getPlayer() != null) {
            List<Integer> effectIdsToRemove = new ArrayList<>();
            if (shouldRemoveSlowness.getValue()) mc.getPlayer().removePotionEffectClient(Potion.moveSlowdown.id);
            if (shouldRemoveMiningFatigue.getValue()) mc.getPlayer().removePotionEffectClient(Potion.digSlowdown.id);
            if (shouldRemoveBlindness.getValue()) mc.getPlayer().removePotionEffectClient(Potion.blindness.id);
            if (shouldRemoveWeakness.getValue()) mc.getPlayer().removePotionEffectClient(Potion.weakness.id);
            if (shouldRemoveWither.getValue()) effectIdsToRemove.add(Potion.wither.id);
            if (shouldRemovePoison.getValue()) effectIdsToRemove.add(Potion.poison.id);
            if (shouldRemoveWaterBreathing.getValue()) effectIdsToRemove.add(Potion.waterBreathing.id);

            for (Integer effectId : effectIdsToRemove) {
                mc.getPlayer().removePotionEffectClient(effectId);
            }
        }
    }
}
