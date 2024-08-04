// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.network.PacketUtil;
import cc.slack.utils.network.PingSpoofUtil;
import cc.slack.utils.network.TimedPacket;
import cc.slack.utils.other.PrintUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.S00PacketServerInfo;

import java.util.ArrayList;

@ModuleInfo(
        name = "Backtrack",
        category = Category.GHOST
)
public class Backtrack extends Module {

    private final NumberValue<Integer> maxDelay = new NumberValue<>("Max Delay", 4, 1, 40, 1);
    private final NumberValue<Integer> backtrackTime = new NumberValue<>("Backtrack ticks", 20, 10, 60, 1);

    private int ticksSinceAttack = 0;
    private int backtrackTicks = 0;
    private boolean enabled = false;
    public EntityPlayer player;

    private int comboCounter = 0;
    private boolean sentHit = false;

    public Backtrack() {
        addSettings(maxDelay, backtrackTime);
    }

    @Override
    public void onEnable() {
        ticksSinceAttack = 0;
        backtrackTicks = 0;
        enabled = false;
    }

    @SuppressWarnings("unused")
    @Listen
    public void onUpdate (UpdateEvent event) {
        if (mc.thePlayer == null || mc.getWorld() == null) {
            enabled = false;
            PingSpoofUtil.disable();
        }

        if (enabled) {
            if (ticksSinceAttack < maxDelay.getValue() * 3) {
                ticksSinceAttack ++;
            }
        }
        if (backtrackTicks > 0) {
            backtrackTicks --;
        } else {
            if (enabled) {
                PingSpoofUtil.disable();
                enabled = false;
            }
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        final Packet packet = event.getPacket();

        if (event.getDirection() == PacketDirection.OUTGOING) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity wrapper = (C02PacketUseEntity) packet;
                if (wrapper.getEntityFromWorld(mc.getWorld()) instanceof EntityPlayer && wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    if (mc.thePlayer.ticksSinceLastDamage > 20 && comboCounter > 1) {
                        if (backtrackTicks == 0) ticksSinceAttack = 0;
                        backtrackTicks = backtrackTime.getValue();
                        enabled = true;
                        PingSpoofUtil.enableInbound(true, ticksSinceAttack * 17);
                        PingSpoofUtil.enableOutbound(true, ticksSinceAttack * 10);

                        player = (EntityPlayer) wrapper.getEntityFromWorld(mc.getWorld());
                    }
                    if (((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld).hurtResistantTime == 0 && !sentHit) {
                        sentHit = true;
                        comboCounter += 1;
                    } else if (((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld).hurtResistantTime > 2) {
                        sentHit = false;
                    }
                }
            }
        } else {
            if (packet instanceof S12PacketEntityVelocity) {
                if (((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId())
                    ticksSinceAttack /= 3;
            }
        }
    }


    @Override
    public void onDisable() {
        PingSpoofUtil.disable(true, true);
    }
}
