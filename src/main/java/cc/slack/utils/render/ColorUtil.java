package cc.slack.utils.render;

import cc.slack.Slack;
import cc.slack.features.modules.impl.render.HUD;
import cc.slack.utils.client.mc;
import java.awt.*;

public class ColorUtil extends mc {
    public enum themeStyles {
        RAINBOW, SLACK_STATIC, SLACK, ASTOLFO, CHRISTMAS, CUSTOM
    }


    public static Color rainbow(final int count, final float bright, final float st) {
        double v1 = Math.ceil((double)(System.currentTimeMillis() + count * 109)) / 5.0;
        return Color.getHSBColor(((float)((v1 %= 360.0) / 360.0) < 0.5) ? (-(float)(v1 / 360.0)) : ((float)(v1 / 360.0)), st, bright);
    }

    public static Color getThemeColor(themeStyles t, boolean start) {
        HUD hud = Slack.getInstance().getModuleManager().getInstance(HUD.class);
        if (start) {
            switch (t) {
                case SLACK_STATIC:
                    return new Color(90, 150, 200);
                case SLACK:
                    return new Color(59, 145, 217);
                case ASTOLFO:
                    return new Color(81, 230, 230);
                case CHRISTMAS:
                    return new Color(240, 81, 81);
                case CUSTOM:
                    return new Color(hud.r1.getValue(), hud.g1.getValue(), hud.b1.getValue());
            }
        } else {
            switch (t) {
                case SLACK_STATIC:
                    return new Color(90, 150, 200);
                case SLACK:
                    return new Color(34, 65, 112);
                case ASTOLFO:
                    return new Color(216, 112, 206);
                case CHRISTMAS:
                    return new Color(240, 240, 240);
                case CUSTOM:
                    return new Color(hud.r2.getValue(), hud.g2.getValue(), hud.b2.getValue());
            }
        }
        return new Color(1,1,1);
    }

    public static Color getColor() {
        return getColor(true);
    }

    public static Color getColor(boolean timeMove) {
        return getColor(0.0, timeMove);
    }

    public static Color getColor(double r) {
        return getColor(r, true);
    }

    public static Color getColor(double r, boolean timeMove) {
        return getColor(Slack.getInstance().getModuleManager().getInstance(HUD.class).theme.getValue(), r, timeMove);
    }

    public static Color getColor(themeStyles t, double r) {
        return getColor(t, r, true);
    }
    public static Color getColor(themeStyles t, double r, boolean timeMove) {
        if (timeMove) {
            r += System.currentTimeMillis() / 3000.0;
        }
        if (t == themeStyles.RAINBOW) {
            return rainbow(-100 + (int) (r * 10), 1.0f, 0.47f);
        }
        r = r % 2;
        if (r < 1) {
            return mix (getThemeColor(t, true), getThemeColor(t, false), r);
        } else {
            return mix (getThemeColor(t, false), getThemeColor(t, true), r - 1);
        }
    }

    public static Color mix(Color c1, Color c2, Double percent) {
        int r = (int) (c1.getRed() * (1 - percent) + c2.getRed() * percent);
        int g = (int) (c1.getGreen() * (1 - percent) + c2.getGreen() * percent);
        int b = (int) (c1.getBlue() * (1 - percent) + c2.getBlue() * percent);
        int a = (int) (c1.getAlpha() * (1 - percent) + c2.getAlpha() * percent);
        return new Color(r,g,b,a);
    }
}
