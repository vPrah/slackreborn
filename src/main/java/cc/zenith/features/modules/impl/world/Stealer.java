package cc.zenith.features.modules.impl.world;

import cc.zenith.events.impl.player.UpdateEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.MC;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.inventory.GuiChest;

@ModuleInfo(
        name = "Stealer",
        category = Category.WORLD
)
public class Stealer extends Module {

    @Listen
    public void onUpdate (UpdateEvent event) {
        }
    }
