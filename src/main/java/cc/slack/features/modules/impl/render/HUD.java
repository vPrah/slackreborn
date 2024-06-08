// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.impl.render.hud.arraylist.IArraylist;
import cc.slack.features.modules.impl.render.hud.arraylist.impl.*;
import cc.slack.features.modules.impl.world.Scaffold;
import cc.slack.utils.client.mc;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.player.MovementUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.round;
import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(name = "HUD", category = Category.RENDER)
public class HUD extends Module {
	private final ModeValue<IArraylist> arraylistModes = new ModeValue<>("Arraylist", new IArraylist[] { new BasicArrayList(), new Basic2ArrayList(), new RavenArrayList()});
	public final BooleanValue binds = new BooleanValue("Display Binds", false);

	private final ModeValue<String> watermarksmodes = new ModeValue<>("WaterMark", new String[] { "Classic", "Classic2", "Backgrounded", "Backgrounded2", "BackgroundedRound", "BackgroundedRound2", "Logo" });

	public final BooleanValue notification = new BooleanValue("Notifications", true);
	public final BooleanValue roundednotification = new BooleanValue("Rounded Notifications", false);


	private final BooleanValue fpsdraw = new BooleanValue("FPS Counter", true);
	private final BooleanValue bpsdraw = new BooleanValue("BPS Counter", true);

	private final BooleanValue scaffoldDraw = new BooleanValue("Scaffold Counter", true);

	public final BooleanValue sound = new BooleanValue("Toggle Sound", false);

	private int scaffoldTicks = 0;
	private String displayString = " ";
	private final ResourceLocation imageResource = new ResourceLocation("slack/menu/hud.jpg");
	private ArrayList<String> notText = new ArrayList<>();
	private ArrayList<Long> notEnd = new ArrayList<>();
	private ArrayList<Long> notStart = new ArrayList<>();
	private ArrayList<String> notDetailed = new ArrayList<>();
	private ArrayList<Slack.NotificationStyle> notStyle = new ArrayList<>();

	public HUD() {
		addSettings(arraylistModes, binds, watermarksmodes, notification, roundednotification, fpsdraw, bpsdraw, scaffoldDraw, sound);
	}

	@Listen
	public void onUpdate(UpdateEvent e) {
		arraylistModes.getValue().onUpdate(e);
	}

	@Listen
	public void onRender(RenderEvent e) {
		if (e.state != RenderEvent.State.RENDER_2D) return;

		arraylistModes.getValue().onRender(e);

		switch (watermarksmodes.getValue()) {
			case "Classic":
				Fonts.apple24.drawStringWithShadow("S", 4, 4, 0x5499C7);
				Fonts.apple24.drawStringWithShadow("lack", 11, 4, -1);
				break;
			case "Classic2":
				Fonts.poppins24.drawStringWithShadow("S", 4, 4, 0x5499C7);
				Fonts.poppins24.drawStringWithShadow("lack", 11, 4, -1);
				break;
			case "Backgrounded":
				drawRect(2, 2, 55 + Fonts.apple18.getStringWidth(" - " + Minecraft.getDebugFPS()), 15, 0x80000000);
				Fonts.apple18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 5, 0x5499C7);
				Fonts.apple18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 5, -1);
				break;
			case "Backgrounded2":
				drawRect(2, 2, 55 + Fonts.poppins18.getStringWidth(" - " + Minecraft.getDebugFPS()), 15, 0x80000000);
				Fonts.poppins18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 5, 0x5499C7);
				Fonts.poppins18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 5, -1);
				break;
			case "BackgroundedRound":
				drawRoundedRect(2, 2, 55 + Fonts.apple18.getStringWidth(" - " + Minecraft.getDebugFPS()) - 2, 15 - 2, 4.0f, 0x80000000);
				Fonts.apple18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 5, 0x5499C7);
				Fonts.apple18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 5, -1);
				break;
			case "BackgroundedRound2":
				drawRoundedRect(2, 2, 57 + Fonts.apple18.getStringWidth(" - " + Minecraft.getDebugFPS()) - 2, 16 - 2, 4.0f, 0x80000000);
				Fonts.poppins18.drawStringWithShadow("Slack " + Slack.getInstance().getInfo().getVersion(), 4, 5, 0x5499C7);
				Fonts.poppins18.drawStringWithShadow(" - " + Minecraft.getDebugFPS(), 53, 5, -1);
				break;
			case "Logo":

				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				RenderUtil.drawImage(new ResourceLocation("slack/menu/hud.png"), 4, 4, 20, 33);
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();

				break;
		}
		if (fpsdraw.getValue()) {
			Fonts.apple18.drawStringWithShadow("FPS:  ", 4, mc.getScaledResolution().getScaledHeight() - 10, 0x5499C7);
			Fonts.apple18.drawStringWithShadow("" + Minecraft.getDebugFPS(), 25, mc.getScaledResolution().getScaledHeight() - 10, -1);
		}

		if (bpsdraw.getValue()) {
			Fonts.apple18.drawStringWithShadow("BPS:  ", 50, mc.getScaledResolution().getScaledHeight() - 10, 0x5499C7);
			Fonts.apple18.drawStringWithShadow(getBPS(), 71, mc.getScaledResolution().getScaledHeight() - 10, -1);

		}

		if (scaffoldDraw.getValue()) {
			if (Slack.getInstance().getModuleManager().getInstance(Scaffold.class).isToggle()) {
				if (mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem + 36).getStack() != null) {
					displayString = mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem + 36).getStack().stackSize + " blocks";
				} else {
					displayString = "No blocks";
				}
				if (scaffoldTicks < 10)
					scaffoldTicks++;
			} else {
				if (scaffoldTicks > 0)
					scaffoldTicks--;
			}

			if (scaffoldTicks != 0) {
				ScaledResolution sr = mc.getScaledResolution();
				if (mc.getPlayer().inventoryContainer.getSlot(mc.getPlayer().inventory.currentItem + 36).getStack() != null) {
					int y = (int) ((1 - Math.pow(1 - (scaffoldTicks / 10.0), 3)) * 20);
					RenderUtil.drawRoundedRect(
							((sr.getScaledWidth() -  Fonts.apple18.getStringWidth(displayString)) / 2f) - 4,
							sr.getScaledHeight() * 3f / 4F - 4f - y,
							((sr.getScaledWidth() +  Fonts.apple18.getStringWidth(displayString)) / 2f) + 4,
							sr.getScaledHeight() * 3f / 4F + mc.getFontRenderer().FONT_HEIGHT + 2f - y,
							2, 0x80000000);
					Fonts.apple18.drawString(displayString, (sr.getScaledWidth() - Fonts.apple18.getStringWidth(displayString)) / 2f, sr.getScaledHeight() * 3f / 4F - y, new Color(255, 255, 255).getRGB(), false);
				}
			}
		}

		if (notification.getValue()) {
			int y = mc.getScaledResolution().getScaledHeight() - 10;
			for (int i = 0; i < notText.size(); i++) {
				double x = getXpos(notStart.get(i), notEnd.get(i));
				renderNotification((int) (mc.getScaledResolution().getScaledWidth() - 10 + 160 * x), y, notText.get(i), notDetailed.get(i), notStyle.get(i));
				if (roundednotification.getValue()) {
					y -= (int) (Math.pow((1 - x), 0.5) * 23);
				} else {
					y -= (int) (Math.pow((1 - x), 0.5) * 19);
				}
			}

			ArrayList<Integer> removeList = new ArrayList();

			for (int i = 0; i < notText.size(); i++) {
				if (System.currentTimeMillis() > notEnd.get(i)) {
					removeList.add(i);
				}
			}

			Collections.reverse(removeList);

			for (int i : removeList) {
				notText.remove(i);
				notEnd.remove(i);
				notStart.remove(i);
				notDetailed.remove(i);
				notStyle.remove(i);
			}
		} else {
			notText.clear();
			notEnd.clear();
			notStart.clear();
			notDetailed.clear();
			notStyle.clear();
		}
	}

	private String getBPS() {
		double currentBPS = ((double) round((MovementUtil.getSpeed() * 20) * 100)) / 100;
		return String.format("%.2f", currentBPS);
	}

	private void renderNotification(int x, int y, String bigText, String smallText, Slack.NotificationStyle style) {
		int color = new Color(50, 50, 50, 120).getRGB();
		switch (style) {
			case GRAY:
				break;
			case SUCCESS:
				color = new Color(23, 138, 29, 120).getRGB();
				break;
			case FAIL:
				color = new Color(148, 36, 24, 120).getRGB();
				break;
			case WARN:
				color = new Color(156, 128, 37, 120).getRGB();
				break;
		}
		if (roundednotification.getValue()) {
			RenderUtil.drawRoundedRect(
					x - 10 - Fonts.apple18.getStringWidth(bigText),
					y - 10 - Fonts.apple18.getHeight(), x, y,
					2, color);
			Fonts.apple18.drawStringWithShadow(bigText, x - 5 - Fonts.apple18.getStringWidth(bigText),
					y - 5 -Fonts.apple18.getHeight(), new Color(255, 255, 255).getRGB());
			Fonts.apple18.drawStringWithShadow(bigText, x - 5 - Fonts.apple18.getStringWidth(bigText),
					y - 5 - Fonts.apple18.getHeight(), new Color(255, 255, 255).getRGB());
		} else {
			drawRect(x - 6 - Fonts.apple18.getStringWidth(bigText), y - 6 - Fonts.apple18.getHeight(), x, y,
					color);
			Fonts.apple18.drawStringWithShadow(bigText, x - 3 - Fonts.apple18.getStringWidth(bigText),
					y - 3 - Fonts.apple18.getHeight(), new Color(255, 255, 255).getRGB());
		}
	}

/*
	private void renderNotification(int x, int y, String bigText, String smallText, Slack.NotificationStyle style) {
		int color = new Color(50, 50, 50, 120).getRGB();
		switch (style) {
		case GRAY:
			break;
		case SUCCESS:
			color = new Color(23, 138, 29, 120).getRGB();
			break;
		case FAIL:
			color = new Color(148, 36, 24, 120).getRGB();
			break;
		case WARN:
			color = new Color(156, 128, 37, 120).getRGB();
			break;
		}
		if (roundednotification.getValue()) {
			RenderUtil.drawRoundedRect(
					x - 10 - mc.getFontRenderer().getStringWidth(bigText),
					y - 10 - mc.getFontRenderer().FONT_HEIGHT, x, y,
					2, color);
			mc.getFontRenderer().drawString(bigText, x - 5 - mc.getFontRenderer().getStringWidth(bigText),
					y - 5 - mc.getFontRenderer().FONT_HEIGHT, new Color(255, 255, 255).getRGB(), false);
			mc.getFontRenderer().drawString(bigText, x - 5 - mc.getFontRenderer().getStringWidth(bigText),
					y - 5 - mc.getFontRenderer().FONT_HEIGHT, new Color(255, 255, 255).getRGB(), false);
		} else {
			drawRect(x - 6 - mc.getFontRenderer().getStringWidth(bigText), y - 6 - mc.getFontRenderer().FONT_HEIGHT, x, y,
					color);
			mc.getFontRenderer().drawString(bigText, x - 3 - mc.getFontRenderer().getStringWidth(bigText),
					y - 3 - mc.getFontRenderer().FONT_HEIGHT, new Color(255, 255, 255).getRGB());
		}
	}

 */

	private double getXpos(Long startTime, Long endTime) {
		if (endTime - System.currentTimeMillis() < 300L) {
			return Math.pow(1 - (endTime - System.currentTimeMillis()) / 300f, 3);
		} else if (System.currentTimeMillis() - startTime < 300L) {
			return Math.pow( 1- (System.currentTimeMillis() - startTime) / 300f, 3);
		} else {
			return 0.0;
		}
	}

	public void addNotification(String bigText, String smallText, Long duration, Slack.NotificationStyle style) {
		if (!notification.getValue()) return;
		notText.add(bigText);
		notEnd.add(System.currentTimeMillis() + duration);
		notStart.add(System.currentTimeMillis());
		notDetailed.add(smallText);
		notStyle.add(style);
	}

	private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
		RenderUtil.drawRoundedRect(x, y, x + width, y + height, radius, color);
	}


	@Override
	public String getMode() { return arraylistModes.getValue().toString(); }
}
