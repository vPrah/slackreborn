package cc.slack.features.modules.impl.render;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

@ModuleInfo(
        name = "XRay",
        category = Category.RENDER
)
public class XRay extends Module {

    private float oldgammavalue;
    private final int[] blockIds = new int[]{14, 15, 56, 129};


    @Override
    public void onEnable() {
        oldgammavalue = mc.getGameSettings().gammaSetting;
        mc.getGameSettings().gammaSetting = 10.0f;
        mc.getGameSettings().ambientOcclusion = 0;
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        mc.getGameSettings().gammaSetting = oldgammavalue;
        mc.getGameSettings().ambientOcclusion = 1;
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    public boolean shouldRenderBlock(Block block) {
        for (int id : this.blockIds) {
            if (block != Block.getBlockById(id)) continue;
            return true;
        }
        return false;
    }
}
