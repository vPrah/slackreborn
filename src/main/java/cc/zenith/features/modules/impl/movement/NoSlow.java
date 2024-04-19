package cc.zenith.features.modules.impl.movement;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.features.modules.api.settings.impl.ModeValue;
import cc.zenith.utils.client.MC;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

@ModuleInfo(
        name = "NoSlow",
        category = Category.MOVEMENT
)

public class NoSlow extends Module {

    private final ModeValue<String> mode = new ModeValue<>("Mode", new String[]{"Vanilla", "NCPLatest"});

    public NoSlow() {
        addSettings(mode);
    }

    @Listen
    public void onUpdate (UpdateEvent event) {
        if (MC.getPlayer().isUsingItem() && (MC.getPlayer().getHeldItem().item instanceof ItemFood)) {
            switch (mode.getValue().toLowerCase()) {
                case "vanilla":
                    break;
                case "ncplatest":
                    // halflin codeate el puto bypass del noslow
                   break;
            }
        }
    }

}
