package cc.slack.utils.drag;

import cc.slack.utils.client.mc;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
@AllArgsConstructor
public class DragUtil {
	
	/*
	 * Only for render 2d for now..
	 */
	
	private double x, y, width, height;
	private float scale;

	public boolean isInside(int x, int y) {
		return x > getX() && y > getY() && x < getX() + getWidth() && y < getY() + getHeight();
	}

	public static double[] setScaledPosition(double x, double y) {
		ScaledResolution sr = new ScaledResolution(mc.getMinecraft());

		double width = sr.getScaledWidth();
		double height = sr.getScaledHeight_double();

		return new double[] { width / 1000F * x, height / 1000F * y };
	}

	public static double[] setPosition(double x, double y) {
		ScaledResolution sr = new ScaledResolution(mc.getMinecraft());

		double width = sr.getScaledWidth();
		double height = sr.getScaledHeight_double();

		return new double[] { x * 1000F / width, y * 1000F / height };
	}
}
