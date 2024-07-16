package cc.slack.features.modules.impl.render;

import java.util.Collection;

import cc.slack.events.impl.render.RenderEvent;
import cc.slack.events.impl.render.RenderScoreboard;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.BooleanValue;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.utils.drag.DragUtil;
import cc.slack.utils.font.Fonts;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatFormatting;

import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(name = "Scoreboard", category = Category.RENDER)
public class ScoreboardModule extends Module {

	private final BooleanValue noscoreboard = new BooleanValue("No Scoreboard", false);
	private final BooleanValue roundedValue = new BooleanValue("Rounded", false);
	private final BooleanValue textShadow = new BooleanValue("Text Shadow", false);
	private final ModeValue<String> scoreboardFont = new ModeValue<>("Scoreboard Font", new String[]{"Minecraft", "Apple", "Poppins", "Roboto"});
	private final ModeValue<String> scoreboardFontScale = new ModeValue<>("Font Scale", new String[]{"18","20", "24"});

	double posX = 0.0D;
	double posY = 30.0D;


	public ScoreboardModule() {
		addSettings(noscoreboard, roundedValue, textShadow, scoreboardFont, scoreboardFontScale);
	}

	@Listen
	public void onRenderScoreboard(RenderScoreboard event) {
		event.cancel();
	}

	@Listen
	public void onRender(RenderEvent event) {
		if (event.getState() != RenderEvent.State.RENDER_2D) return;
		if (noscoreboard.getValue()) return;

		ScaledResolution scaledRes = new ScaledResolution(mc);
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		ScoreObjective scoreobjective = null;
		ScoreObjective objective = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);

		if (objective == null) return;

		double[] pos = DragUtil.setScaledPosition(this.posX, this.posY);
		scoreboard = objective.getScoreboard();
		Collection<Score> collection = scoreboard.getSortedScores(objective);

		double i = 70;
		if (mc.MCfontRenderer.getStringWidth(objective.getDisplayName()) > i) {
			i = mc.MCfontRenderer.getStringWidth(objective.getDisplayName());
		}
		double width = i;
		for (Score score2 : collection) {
			ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam2, score2.getPlayerName()) + ": " + ChatFormatting.RED + score2.getScorePoints();

			if (width < mc.MCfontRenderer.getStringWidth(s1)) {
				width = mc.MCfontRenderer.getStringWidth(s1);
			}
		}

		int height = collection.size() * 9 + 16;
		if (roundedValue.getValue()) {
			RenderUtil.drawRoundedRect((float) pos[0], (float) pos[1], (float) (pos[0] + width), (float) (pos[1] + height), 8F, 1342177280);
		} else {
			drawRect(pos[0], pos[1] + 2 , pos[0] + width, pos[1] + 12, 1610612736); // top
			drawRect(pos[0], pos[1] + 2, (pos[0] + width), pos[1] + height, 1342177280);
		}

		switch (scoreboardFont.getValue()) {
			case "Minecraft":
				mc.MCfontRenderer.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - mc.MCfontRenderer.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
				break;
			case "Apple":
				switch (scoreboardFontScale.getValue()) {
					case "18":
						Fonts.apple18.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.apple18.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "20":
						Fonts.apple20.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.apple20.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "24":
						Fonts.apple24.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.apple24.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
				}
				break;
			case "Poppins":
				switch (scoreboardFontScale.getValue()) {
					case "18":
						Fonts.poppins18.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.poppins18.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "20":
						Fonts.poppins20.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.poppins20.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "24":
						Fonts.poppins24.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.poppins24.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
				}
				break;
			case "Roboto":
				switch (scoreboardFontScale.getValue()) {
					case "18":
						Fonts.roboto18.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.roboto18.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "20":
						Fonts.roboto20.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.roboto20.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
					case "24":
						Fonts.roboto24.drawString(objective.getDisplayName(), (float) ((float) pos[0] + width / 2 - Fonts.roboto24.getStringWidth(objective.getDisplayName()) / 2), (float) pos[1] + 4, -1, textShadow.getValue());
						break;
				}
				break;
		}
		int j = 0;
		for (Score score1 : collection) {
			++j;
			ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
			String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
			String s2 = ChatFormatting.RED + "" + score1.getScorePoints();
			switch (scoreboardFont.getValue()) {
				case "Minecraft":
					mc.MCfontRenderer.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
					mc.MCfontRenderer.drawString(s2, (float) (pos[0] + width - mc.MCfontRenderer.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());
					break;
				case "Apple":
					switch (scoreboardFontScale.getValue()) {
						case "18":
							Fonts.apple18.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.apple18.drawString(s2, (float) (pos[0] + width - Fonts.apple18.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "20":
							Fonts.apple20.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.apple20.drawString(s2, (float) (pos[0] + width - Fonts.apple20.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "24":
							Fonts.apple24.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.apple24.drawString(s2, (float) (pos[0] + width - Fonts.apple24.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
					}
					break;
				case "Poppins":
					switch (scoreboardFontScale.getValue()) {
						case "18":
							Fonts.poppins18.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.poppins18.drawString(s2, (float) (pos[0] + width - Fonts.poppins18.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "20":
							Fonts.poppins20.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.poppins20.drawString(s2, (float) (pos[0] + width - Fonts.poppins20.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "24":
							Fonts.poppins24.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.poppins24.drawString(s2, (float) (pos[0] + width - Fonts.poppins24.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
					}
					break;
				case "Roboto":
					switch (scoreboardFontScale.getValue()) {
						case "18":
							Fonts.roboto18.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.roboto18.drawString(s2, (float) (pos[0] + width - Fonts.roboto18.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "20":
							Fonts.roboto20.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.roboto20.drawString(s2, (float) (pos[0] + width - Fonts.roboto20.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
						case "24":
							Fonts.roboto24.drawString(s1, (float) pos[0] + 3, (float) ((float) pos[1] + height - 9.2 * j), -1, textShadow.getValue());
							Fonts.roboto24.drawString(s2, (float) (pos[0] + width - Fonts.roboto24.getStringWidth(s2)), (float) (pos[1] + height - 9 * j), -1, textShadow.getValue());							break;
					}
					break;
			}
		}
	}
	

	@Override
	public DragUtil getPosition() {
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
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
			if (width < mc.MCfontRenderer.getStringWidth(s1)) {
				width = mc.MCfontRenderer.getStringWidth(s1);
			}
		}

		double[] pos = DragUtil.setScaledPosition(this.posX, this.posY);

		int height = collection.size() * 9 + 16;
		return new DragUtil(pos[0], pos[1], width, height, 1);
	}

	@Override
	public void setXYPosition(double x, double y) {
		this.posX = (x);
		this.posY = (y);
	}
}
