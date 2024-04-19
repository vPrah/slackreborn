package cc.slack.utils.font;

import cc.slack.utils.other.PrintUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class Fonts {
    public static final MCFontRenderer IconFont = new MCFontRenderer(fontFromTTF("guiicons.ttf", 24), true, true);
    public static final MCFontRenderer IconFontBig = new MCFontRenderer(fontFromTTF("guiicons.ttf", 38), true, true);

    public static final MCFontRenderer SFBold30 = new MCFontRenderer(fontFromTTF("sfsemibold.ttf", 30), true, true);
    public static final MCFontRenderer SFBold18 = new MCFontRenderer(fontFromTTF("sfsemibold.ttf", 18), true, true);
    public static final MCFontRenderer SFReg18 = new MCFontRenderer(fontFromTTF("sfregular.ttf", 18), true, true);
    public static final MCFontRenderer SFReg24 = new MCFontRenderer(fontFromTTF("sfregular.ttf", 24), true, true);
    public static final MCFontRenderer SFReg45 = new MCFontRenderer(fontFromTTF("sfregular.ttf", 45), true, true);
    public static final MCFontRenderer axi24 = new MCFontRenderer(fontFromTTF("axi.ttf", 24), true, false);
    public static final MCFontRenderer axi12 = new MCFontRenderer(fontFromTTF("axi.ttf", 12), true, false);
    public static final MCFontRenderer axi16 = new MCFontRenderer(fontFromTTF("axi.ttf", 16), true, false);
    public static final MCFontRenderer axi18 = new MCFontRenderer(fontFromTTF("axi.ttf", 18), true, false);
    public static final MCFontRenderer axi45 = new MCFontRenderer(fontFromTTF("axi.ttf", 45), true, false);

    public static final MCFontRenderer apple18 = new MCFontRenderer(fontFromTTF("apple.ttf", 18), true, false);
    public static final MCFontRenderer apple24 = new MCFontRenderer(fontFromTTF("apple.ttf", 24), true, false);
    //public static final MCFontRenderer hearts18 = new MCFontRenderer(fontFromTTF("hearts.ttf",18), true, true);
    public static final MCFontRenderer Arial18 = new MCFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
    public static final MCFontRenderer Arial45 = new MCFontRenderer(new Font("Arial", Font.PLAIN, 45), true, false);
    public static final MCFontRenderer Arial65 = new MCFontRenderer(new Font("Arial", Font.PLAIN, 65), true, false);
    public static final MCFontRenderer Checkmark = new MCFontRenderer(fontFromTTF("checkmark.ttf", 24), true, false);

    private static Font fontFromTTF(String fileName, float fontSize) {
        PrintUtil.print("Initializing Font: " + fileName + " | Size: " + fontSize);
        Font output = null;
        try {
            output = Font.createFont(Font.TRUETYPE_FONT, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("slack/fonts/" + fileName)).getInputStream());
            output = output.deriveFont(fontSize);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return output;
    }

}
