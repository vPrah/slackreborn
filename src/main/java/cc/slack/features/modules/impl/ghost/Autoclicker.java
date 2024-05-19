// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.other.TimeUtil;
import cc.slack.utils.player.AttackUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemSword;
import net.minecraft.client.settings.KeyBinding;

@ModuleInfo(
        name = "Autoclicker",
        category = Category.GHOST
)
public class Autoclicker extends Module {

    public final NumberValue<Float> targetCPS = new NumberValue<>("Target CPS", 11f, 0f, 30f, 0.1f);

    public final NumberValue<Float> randomizeAmount = new NumberValue<>("Randomization Amount", 1.5f, 0f, 4f, 0.1f);
    public final ModeValue<String> randomizeMode = new ModeValue<>("Randomization Pattern", new String[]{"NONE", "OLD", "NEW", "EXTRA"});

    public final BooleanValue onlySword = new BooleanValue("Only Sword", false);

    public final ModeValue<String> autoblockMode = new ModeValue<>("Autoblock", new String[]{"Off", "Click", "Normal", "BlockHit"});
    public final BooleanValue autoblockOnClick = new BooleanValue("Block On Mouse Down", true);

    public Autoclicker() {
        super();
        addSettings(targetCPS, randomizeAmount, randomizeMode, onlySword, autoblockMode, autoblockOnClick);
    }

    private final TimeUtil leftClickTimer = new TimeUtil();
    private long leftClickDelay = 0L;

    @SuppressWarnings("unused")
    @Listen
    public void onRender(RenderEvent event) {
        if (
                GameSettings.isKeyDown(mc.getGameSettings().keyBindAttack)
                && (!onlySword.getValue() || (mc.getPlayer().getHeldItem() != null? mc.getPlayer().getHeldItem().getItem() instanceof ItemSword : false))
                && !mc.getPlayerController().isHittingBlock
        ) {
            if (leftClickTimer.hasReached(leftClickDelay)) {
                leftClickTimer.reset();
                leftClickDelay = updateDelay(targetCPS.getValue(), randomizeAmount.getValue());
                KeyBinding.onTick(mc.getGameSettings().keyBindAttack.getKeyCode());
                if (autoblockMode.getValue().equals("Click") && (!autoblockOnClick.getValue() || GameSettings.isKeyDown(mc.getGameSettings().keyBindUseItem))) KeyBinding.onTick(mc.getGameSettings().keyBindUseItem.getKeyCode());
            }
            boolean rightMouseDown = GameSettings.isKeyDown(mc.getGameSettings().keyBindUseItem);
            switch (autoblockMode.getValue().toLowerCase()) {
                case "off":
                    break;
                case "click":
                    rightMouseDown = false;
                    break;
                case "normal":
                    rightMouseDown = leftClickTimer.elapsed() > 0.1 * leftClickDelay && leftClickTimer.elapsed() < 0.65 * leftClickDelay;
                    break;
                case "blockhit":
                    rightMouseDown = leftClickTimer.elapsed() < 0.4 * leftClickDelay;
                    break;
            }
            mc.getGameSettings().keyBindUseItem.pressed = rightMouseDown && (!autoblockOnClick.getValue() || GameSettings.isKeyDown(mc.getGameSettings().keyBindUseItem));
        }
    }

    private long updateDelay(Float cps, Float rand) {
        switch (randomizeMode.getValue().toLowerCase()) {
            case "none":
                return (long) (1000 / cps);
            case "old":
                return (long) (1000 / AttackUtil.getOldRandomization(cps, rand));
            case "new":
                return (long) (1000 / AttackUtil.getNewRandomization(cps, rand));
            case "extra":
                return (long) (1000 / AttackUtil.getExtraRandomization(cps, rand));
        }
        return 0L;
    }

}
