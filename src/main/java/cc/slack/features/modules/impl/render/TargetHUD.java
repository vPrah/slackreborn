// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import java.awt.Color;

import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.drag.DragUtil;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

import javax.vecmath.Vector4d;

@ModuleInfo(name = "TargetHUD", category = Category.RENDER)
public class TargetHUD extends Module {

	private final ModeValue<String> mode = new ModeValue<>(new String[] {"Classic", "Classic2", "Rounded", "Rounded2" });
	private final BooleanValue followTarget = new BooleanValue("Follow Target", false);

	private double posX = 100.0D;
	private double posY = 10.0D;

//	private final NumberValue<Double> posX = new NumberValue<>("PosX", 100.0D, -1000.0D, 1000.0D, 0.1D);
//	private final NumberValue<Double> posY = new NumberValue<>("PosY", 10.0D, -1000.0D, 1000.0D, 0.1D);


	public TargetHUD() {
		addSettings(mode, followTarget);
	}

	private EntityPlayer target;
	private int ticksSinceAttack;

	@Override
	public void onEnable() {
		target = null;
	}

	@Listen
	public void onUpdate(UpdateEvent event) {
		ticksSinceAttack++;

		if (ticksSinceAttack > 20) {
			target = null;
		}

		if (mc.getCurrentScreen() instanceof GuiChat) {
			target = mc.getPlayer();
			ticksSinceAttack = 18;
		}
	}

	@Listen
	public void onPacket(PacketEvent event) {
		if (event.getPacket() instanceof C02PacketUseEntity) {
			C02PacketUseEntity wrapper = event.getPacket();
			if (wrapper.getEntityFromWorld(mc.getWorld()) instanceof EntityPlayer
					&& wrapper.getAction() == C02PacketUseEntity.Action.ATTACK) {
				ticksSinceAttack = 0;
				target = (EntityPlayer) wrapper.getEntityFromWorld(mc.getWorld());
			}
		}
	}

	@Listen
	public void onRender(RenderEvent event) {
		if (event.getState() != RenderEvent.State.RENDER_2D) return;

		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int x = (int) ((sr.getScaledWidth() / 2) + posX), y = (int) ((sr.getScaledHeight() / 2) + posY);

		if (target == null)
			return;

		if (followTarget.getValue()) {
			try {
				Vector4d pos4 = RenderUtil.getProjectedEntity(target, event.getPartialTicks(), 0.7);
				mc.getEntityRenderer().setupOverlayRendering();
				x = ((int) (pos4.x + pos4.z) / 2) + 30;
				y = (int) pos4.y;
			} catch (Exception ignored) {
				// entity is missing
			}
		}

		String targetName = target.getCommandSenderName();
		double offset = -(target.hurtTime * 20);
		double healthPercent = target.getHealth() / target.getMaxHealth();
		Color color = new Color(255, (int) (255 + offset), (int) (255 + offset));
		Boolean winning = target.getHealth() < mc.getPlayer().getHealth();
		Color c = ColorUtil.getColor();

		switch (mode.getValue().toLowerCase()) {
			case "classic":
				drawRect(x, y, 120, 40, new Color(0, 0, 0, 120).getRGB());
				Fonts.poppins18.drawString(targetName, x + 40, y + 8, c.getRGB());
				GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
						color.getAlpha() / 255F);
				mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
				Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 30, 30, 24, 24);
				GlStateManager.color(1, 1, 1, 1);

				drawRect(x + 40, y + 20, 70, 15, new Color(255, 255, 255, 120).getRGB());

				drawRect(x + 40, y + 20, (int) (70 * (target.getHealth() / target.getMaxHealth())), 15,
						c.getRGB());

				String s = (int) (healthPercent * 100) + "%";
				Fonts.poppins18.drawString(s, x + 40 + (70 / 2) - (Fonts.poppins18.getStringWidth(s) / 2),
						y + 20 + (15 / 2) - (Fonts.poppins18.getHeight() / 2) + 1, -1);
				break;
			case "classic2":
				drawRect(x, y, 120, 50, new Color(0, 0, 0, 120).getRGB());

				mc.getFontRenderer().drawString(targetName, x + 35, y + 8, c.getRGB());
				mc.getFontRenderer().drawString(String.format("%.2f", target.getHealth()), x + 35, y + 18, c.getRGB());
				mc.getFontRenderer().drawString(winning ? "W" : "L", x + 107, y + 18, c.getRGB());

				GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
						color.getAlpha() / 255F);
				mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
				Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 25, 25, 24, 24);
				GlStateManager.color(1, 1, 1, 1);

				drawRect(x + 5, y + 35, 110, 10, new Color(255, 255, 255, 120).getRGB());
				drawRect(x + 5, y + 35, (int) (110 * (target.getHealth() / target.getMaxHealth())), 10,
						c.getRGB());
				break;
			case "rounded":
				drawRoundedRect(x, y, 120, 40, 6, new Color(0, 0, 0, 120).getRGB());
				mc.getFontRenderer().drawString(targetName, x + 40, y + 8, c.getRGB());
				GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
						color.getAlpha() / 255F);
				mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
				Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 30, 30, 24, 24);
				GlStateManager.color(1, 1, 1, 1);

				drawRoundedRect(x + 40, y + 20, 70, 15, 2, new Color(255, 255, 255, 120).getRGB());

				drawRoundedRect(x + 40, y + 20, (int) (70 * (target.getHealth() / target.getMaxHealth())), 15, 2,
						c.getRGB());

				String shp = (int) (healthPercent * 100) + "%";
				mc.getFontRenderer().drawString(shp, x + 40 + (70 / 2) - (mc.getFontRenderer().getStringWidth(shp) / 2),
						y + 20 + (15 / 2) - (mc.getFontRenderer().FONT_HEIGHT / 2) + 1, -1);
				mc.getFontRenderer().drawString(shp, x + 40 + (70 / 2) - (mc.getFontRenderer().getStringWidth(shp) / 2),
						y + 20 + (15 / 2) - (mc.getFontRenderer().FONT_HEIGHT / 2) + 1, -1);
				break;
			case "rounded2":
				drawRoundedRect(x, y, 120, 50, 4, new Color(0, 0, 0, 120).getRGB());

				mc.getFontRenderer().drawString(targetName, x + 35, y + 8, c.getRGB());
				mc.getFontRenderer().drawString(String.format("%.2f", target.getHealth()), x + 35, y + 18, c.getRGB());
				mc.getFontRenderer().drawString(winning ? "W" : "L", x + 107, y + 18, c.getRGB());

				GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
						color.getAlpha() / 255F);
				mc.getTextureManager().bindTexture(((AbstractClientPlayer) target).getLocationSkin());
				Gui.drawScaledCustomSizeModalRect(x + 5, y + 5, 3, 3, 3, 3, 25, 25, 24, 24);
				GlStateManager.color(1, 1, 1, 1);

				drawRoundedRect(x + 5, y + 35, 110, 10, 2, new Color(255, 255, 255, 120).getRGB());
				drawRoundedRect(x + 5, y + 35, (int) (110 * (target.getHealth() / target.getMaxHealth())), 10, 2,
						c.getRGB());
				break;
		}
	}
	
	@Override
	public DragUtil getPosition() {
		if (target == null)
			return null;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		double[] pos = DragUtil.setScaledPosition(posX, posY);
		return new DragUtil(pos[0], pos[1], 120, 50, 1);
	}
	
	@Override
	public void setXYPosition(double x, double y) {
		posX = x;
		posY = y;
	}

	private void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
		RenderUtil.drawRoundedRect(x, y, x + width, y + height, radius, color);
	}

	private void drawRect(int x, int y, int width, int height, int color) {
		Gui.drawRect(x, y, x + width, y + height, color);
	}

}
