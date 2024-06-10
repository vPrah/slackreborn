// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.movement;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.rotations.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

@ModuleInfo(
        name = "InvMove",
        category = Category.MOVEMENT
)
public class InvMove extends Module {

    private static final BooleanValue noOpen = new BooleanValue("Cancel Inventory Open", false);
    private static final BooleanValue hypixelTest = new BooleanValue("Hypixel", false);

    public InvMove() {
        super();
        addSettings(noOpen, hypixelTest);
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        MovementUtil.updateBinds(false);
        RotationUtil.updateStrafeFixBinds();
        if (mc.getCurrentScreen() instanceof GuiInventory && hypixelTest.getValue()) {
            if (mc.getPlayer().ticksExisted % 4 == 0) {
                PacketUtil.send(new C0DPacketCloseWindow());
                PacketUtil.send(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
        }
    }

    @Listen
    public void onPacket (PacketEvent event) {
        if (event.getPacket() instanceof C16PacketClientStatus && noOpen.getValue()) {
            if (event.getPacket() == new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
                event.cancel();
            }
        }
    }

}
