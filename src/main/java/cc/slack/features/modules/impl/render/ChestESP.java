// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules.impl.render;

import cc.slack.Slack;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.events.impl.render.RenderEvent;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import java.util.ArrayList;
@ModuleInfo(
        name = "ChestESP",
        category = Category.RENDER
)
public class ChestESP extends Module {

    public ArrayList<BlockPos> chestBoundingBoxes = new ArrayList();

    private final Integer[] chestIDS = new Integer[]{54, 130, 146};

    @Listen
    public void onRender(RenderEvent event) {
        if (event.getState() != RenderEvent.State.RENDER_3D) return;

        for (BlockPos bp : chestBoundingBoxes) {
            ESP.drawAABB(AxisAlignedBB.fromBounds(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + 1, bp.getY() + 1, bp.getZ() + 1));
        }

        chestBoundingBoxes.clear();
    }

    public boolean isChest(Block block) {
        for (int id : chestIDS) {
            if (block != Block.getBlockById(id)) continue;
            return true;
        }
        return false;
    }
    
}
