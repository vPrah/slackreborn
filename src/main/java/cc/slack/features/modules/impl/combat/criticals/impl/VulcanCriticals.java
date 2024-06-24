package cc.slack.features.modules.impl.combat.criticals.impl;

import cc.slack.events.impl.player.AttackEvent;
import cc.slack.features.modules.impl.combat.criticals.ICriticals;
import cc.slack.utils.network.PacketUtil;

public class VulcanCriticals implements ICriticals {

    @Override
    public void onAttack(AttackEvent event) {
        PacketUtil.sendCriticalPacket(0.16477328182606651, false);
        PacketUtil.sendCriticalPacket(0.08307781780646721, false);
        PacketUtil.sendCriticalPacket(0.0030162615090425808, false);
    }

    @Override
    public String toString() {
        return "Vulcan";
    }
}
