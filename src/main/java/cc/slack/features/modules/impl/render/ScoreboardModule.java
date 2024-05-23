package cc.slack.features.modules.impl.render;

import java.awt.Color;
import java.util.Collection;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.events.impl.render.RenderScoreboard;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.client.mc;
import cc.slack.utils.drag.DragUtil;
import cc.slack.utils.render.Render3DUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(name = "Scoreboard", category = Category.RENDER)
public class ScoreboardModule extends Module {

	private final BooleanValue noscoreboard = new BooleanValue("No Scoreboard", false);
	private final BooleanValue roundedValue = new BooleanValue("Rounded", false);
	private final NumberValue<Double> posX = new NumberValue<>("PosX", 0.0D, -1000.0D, 1000.0D, 0.1D);
	private final NumberValue<Double> posY = new NumberValue<>("PosY", 30.0D, -1000.0D, 1000.0D, 0.1D);

	public ScoreboardModule() {
		addSettings(noscoreboard, posX, posY);
	}

	@Listen
	public void onRenderScoreboard(RenderScoreboard event) {
		event.cancel();
	}

	@Listen
	public void onRender(RenderEvent event) {
		if (event.getState() != RenderEvent.State.RENDER_2D) return;
		if (noscoreboard.getValue()) return;
		
		ScaledResolution scaledRes = new ScaledResolution(mc.getMinecraft());
		Scoreboard scoreboard = mc.getWorld().getScoreboard();
		ScoreObjective scoreobjective = null;
		ScoreObjective objective = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
		
		if (objective == null) return;
		
		double[] pos = DragUtil.setScaledPosition(this.posX.getValue(), this.posY.getValue());
		scoreboard = objective.getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(objective);

		double i = 70;
		double width = i;
		for (Score score2 : collection) {
			ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName());

			if (width < mc.getFontRenderer().getStringWidth(s1)) {
				width = mc.getFontRenderer().getStringWidth(s1);
			}
		}

		int height = collection.size() * 9 + 16;
		if (roundedValue.getValue()) {
			Render3DUtil.drawRoundedRect((float) pos[0], (float) pos[1], (float) (pos[0] + width + 5), (float) (pos[1] + height + 5), 8F, 1342177280);
		} else {
		Gui.drawRect(pos[0], pos[1], (pos[0] + width + 5), pos[1] + height, 1342177280);
		}

		mc.getFontRenderer().drawStringWithShadow(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - mc.getFontRenderer().getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1);
		int j = 0;
		for (Score score1 : collection) {
			++j;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
			mc.getFontRenderer().drawStringWithShadow(s1, pos[0] + 3, pos[1] + height - 9 * j, -1);
		}
	}

	@Override
	public DragUtil getPosition() {
		Scoreboard scoreboard = mc.getWorld().getScoreboard();
		ScoreObjective scoreobjective = null;
		ScoreObjective objective = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);

		if (objective == null)
			return new DragUtil(0, 0, 0, 0, 1);

		scoreboard = objective.getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(objective);

		double i = 70;

		double width = i;
		for (Score score2 : collection) {
			ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName());
			if (width < mc.getFontRenderer().getStringWidth(s1)) {
				width = mc.getFontRenderer().getStringWidth(s1);
			}
		}

		double[] pos = DragUtil.setScaledPosition(this.posX.getValue(), this.posY.getValue());

		int height = collection.size() * 9 + 16;
		return new DragUtil(pos[0], pos[1], width, height, 1);
	}

	@Override
	public void setXYPosition(double x, double y) {
		this.posX.setValue(x);
		this.posY.setValue(y);
	}
}
