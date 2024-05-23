// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.ghost;

import org.lwjgl.input.Mouse;

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
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;

@ModuleInfo(name = "Autoclicker", category = Category.GHOST)
public class Autoclicker extends Module {

	public final NumberValue<Float> targetCPS = new NumberValue<>("Target CPS", 11f, 0f, 30f, 0.1f);

	public final NumberValue<Float> randomizeAmount = new NumberValue<>("Randomization Amount", 1.5f, 0f, 4f, 0.1f);
	public final ModeValue<String> randomizeMode = new ModeValue<>("Randomization Pattern",
			new String[] { "NEW", "OLD", "EXTRA", "PATTERN1", "PATTERN2", "NONE" });

	public final BooleanValue onlySword = new BooleanValue("Only Sword", false);

	public final ModeValue<String> autoblockMode = new ModeValue<>("Autoblock",
			new String[] { "Off", "Click", "Normal", "BlockHit" });
	public final BooleanValue autoblockOnClick = new BooleanValue("Block On Mouse Down", true);

	public Autoclicker() {
		addSettings(targetCPS, randomizeAmount, randomizeMode, onlySword, autoblockMode, autoblockOnClick);
	}

	private final TimeUtil leftTimer = new TimeUtil();
	private long leftDelay = 0L;

	@Listen
	public void onRender(RenderEvent event) {
		if (event.getState() != RenderEvent.State.RENDER_3D) return;
		if (mc.getPlayer() == null) return;
		if (checkScreen()) return;

		Mouse.poll();

		if (Mouse.isButtonDown(0)) {
			if (leftTimer.hasReached(leftDelay)) {
				if (onlySword.getValue() && !isHoldingWeapon()) {
					return;
				}

				leftTimer.reset();
				leftDelay = updateDelay(targetCPS.getValue(), randomizeAmount.getValue());
				KeyBinding.setKeyBindState(mc.getGameSettings().keyBindAttack.getKeyCode(), true);
				KeyBinding.onTick(mc.getGameSettings().keyBindAttack.getKeyCode());
			} else if (leftTimer.currentMs > leftDelay * 1000) {
				KeyBinding.setKeyBindState(mc.getGameSettings().keyBindAttack.getKeyCode(), false);
			}
		}

		if (Mouse.isButtonDown(1)) {
			boolean rightMouseDown = GameSettings.isKeyDown(mc.getGameSettings().keyBindUseItem);

			if (autoblockMode.is("Click")) {
				if (autoblockOnClick.getValue()) {
					KeyBinding.onTick(mc.getGameSettings().keyBindUseItem.getKeyCode());
				} else {
					mc.getGameSettings().keyBindUseItem.pressed = rightMouseDown && autoblockOnClick.getValue();
				}
			}

			switch (autoblockMode.getValue().toLowerCase()) {
			case "off":
				break;
			case "click":
				rightMouseDown = false;
				break;
			case "normal":
				rightMouseDown = leftTimer.elapsed() > 0.1 * leftDelay
						&& leftTimer.elapsed() < 0.65 * leftDelay;
				break;
			case "blockhit":
				rightMouseDown = leftTimer.elapsed() < 0.4 * leftDelay;
				break;
			}
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

	public static boolean isHoldingWeapon() {
		if (mc.getPlayer().getCurrentEquippedItem() == null) {
			return false;
		} else {
			Item item = mc.getPlayer().getCurrentEquippedItem().getItem();
			return item instanceof ItemSword;
		}
	}

	private boolean checkScreen() {
		return mc.getCurrentScreen() != null || mc.getCurrentScreen() instanceof GuiInventory
				|| mc.getCurrentScreen() instanceof GuiChest;
	}

}
