// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.combat;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.AttackEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.player.PlayerUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "Criticals",
        category = Category.COMBAT
)
public class Criticals extends Module {

    public final ModeValue<String> criticalMode = new ModeValue<>("Mode", new String[] {"Edit", "Vulcan", "Packet", "Mini"});
    public final BooleanValue onlyGround = new BooleanValue("Only Ground", true);
    
    public Criticals() {
        super();
        addSettings(criticalMode, onlyGround);
    }

    private boolean spoof = false;
    
    @Listen
    public void onAttack(AttackEvent event) {
        switch (criticalMode.getValue().toLowerCase()) {
            case "edit":
                spoof = true;
                break;
            case "vulcan":
                sendPacket(0.16477328182606651, false);
                sendPacket(0.08307781780646721, false);
                sendPacket(0.0030162615090425808, false);
                break;
            case "packet":
                sendPacket(PlayerUtil.HEAD_HITTER_MOTIONY, false);
                break;
            case "mini":
                sendPacket(0.0001, true);
                sendPacket(0.0, false);
                break;    
        }   
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (spoof) {
                ((C03PacketPlayer) event.getPacket()).onGround = false;
                spoof = false;
            }
        }
    }

    private void sendPacket(double yOffset, boolean ground) {
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.getPlayer().posX, mc.getPlayer().posY + yOffset, mc.getPlayer().posZ, ground));
    }
    

}
