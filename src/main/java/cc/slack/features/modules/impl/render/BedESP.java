package cc.slack.features.modules.impl.render;

import cc.slack.events.impl.player.UpdateEvent;
import cc.slack.events.impl.render.RenderEvent;
import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.ModeValue;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import cc.slack.utils.render.ColorUtil;
import cc.slack.utils.render.RenderUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@ModuleInfo(
        name = "BedESP",
        category = Category.RENDER
)
public class BedESP extends Module {

    private final NumberValue<Integer> range = new NumberValue<>("Range", 15, 2, 30, 1);
    private final NumberValue<Double> rate = new NumberValue<>("Rate", 0.4D, 0.1D, 3D, 0.1D);

    // Color
    public final ModeValue<String> colormodes = new ModeValue<>("Color", new String[] { "Client Theme", "Rainbow", "Custom" });
    private final NumberValue<Integer> redValue = new NumberValue<>("Red", 0, 0, 255, 1);
    private final NumberValue<Integer> greenValue = new NumberValue<>("Green", 255, 0, 255, 1);
    private final NumberValue<Integer> blueValue = new NumberValue<>("Blue", 255, 0, 255, 1);
    private final NumberValue<Integer> alphaValue = new NumberValue<>("Alpha", 200, 0, 255, 1);

    private BlockPos[] bed = null;
    private final List<BlockPos[]> beds = new ArrayList();
    private long lastCheck = 0L;

    public BedESP() {
        addSettings(range, rate,colormodes,redValue,greenValue,blueValue,alphaValue);
    }


    @Listen
    public void onUpdate(UpdateEvent event) {
        if (System.currentTimeMillis() - lastCheck >= rate.getValue() * 1000.0) {
            lastCheck = System.currentTimeMillis();

            int rangeValue = range.getValue();
            for (int i = -rangeValue; i <= rangeValue; ++i) {
                for (int j = -rangeValue; j <= rangeValue; ++j) {
                    for (int k = -rangeValue; k <= rangeValue; ++k) {
                        BlockPos blockPos = new BlockPos(mc.thePlayer.posX + j, mc.thePlayer.posY + i, mc.thePlayer.posZ + k);
                        IBlockState getBlockState = mc.theWorld.getBlockState(blockPos);
                        if (getBlockState.getBlock() == Blocks.bed) {
                            beds.add(new BlockPos[]{blockPos, blockPos});
                        }
                    }
                }
            }
        }
    }

    @Listen
    public void onRender(RenderEvent e) {
        if (e.getState() == RenderEvent.State.RENDER_3D) {
            if (BlockPos.nullCheck() && !beds.isEmpty()) {
                Iterator<BlockPos[]> iterator = beds.iterator();
                while (iterator.hasNext()) {
                    BlockPos[] blockPos = iterator.next();
                    if (mc.theWorld.getBlockState(blockPos[0]).getBlock() instanceof BlockBed) {
                        renderBed(blockPos);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
        bed = null;
        beds.clear();
    }

    public int toRGBAHex(float r, float g, float b, float a) {
        return ((int)(a * 255.0F) & 255) << 24 | ((int)(r * 255.0F) & 255) << 16 | ((int)(g * 255.0F) & 255) << 8 | (int)(b * 255.0F) & 255;
    }

    public float[] getColorForTileEntity() {
        Color color = ColorUtil.getColor();
        return new float[]{(float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), 200.0F};
    }

    private void renderBed(BlockPos[] array) {
        Color ct = ColorUtil.getColor();
        double deltaX = (double) array[0].getX() - mc.getRenderManager().viewerPosX;
        double deltaY = (double) array[0].getY() - mc.getRenderManager().viewerPosY;
        double deltaZ = (double) array[0].getZ() - mc.getRenderManager().viewerPosZ;

        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        switch (colormodes.getValue()) {
            case "Client Theme":
                RenderUtil.glColor(ct.getRed(),ct.getGreen(), ct.getBlue(), ct.getAlpha());
                break;
            case "Rainbow":
                RenderUtil.glColor(ColorUtil.rainbow(-100, 1.0f, 0.47f).getRGB());
                break;
            case "Custom":
                RenderUtil.glColor(redValue.getValue(), greenValue.getValue(), blueValue.getValue(), alphaValue.getValue());
                break;
        }
        AxisAlignedBB axisAlignedBB;
        if (array[0].getX() != array[1].getX()) {
            double minX = Math.min(deltaX, deltaX + 1.0);
            double maxX = Math.max(deltaX, deltaX + 2.0);
            axisAlignedBB = new AxisAlignedBB(minX, deltaY, deltaZ, maxX, deltaY + 0.5625, deltaZ + 1.0);
        } else if (array[0].getZ() != array[1].getZ()) {
            double minZ = Math.min(deltaZ, deltaZ - 1.0);
            double maxZ = Math.max(deltaZ, deltaZ + 1.0);
            axisAlignedBB = new AxisAlignedBB(deltaX, deltaY, minZ, deltaX + 1.0, deltaY + 0.5625, maxZ);
        } else {
            double minZ = Math.min(deltaZ, deltaZ);
            double maxZ = Math.max(deltaZ, deltaZ + 2.0);
            axisAlignedBB = new AxisAlignedBB(deltaX, deltaY, minZ, deltaX + 1.0, deltaY + 0.5625, maxZ);
        }

        float[] colors = getColorForTileEntity();
        RenderHelper.drawCompleteBoxFilled(axisAlignedBB, 1.0F, toRGBAHex(colors[0] / 255.0F, colors[1] / 255.0F, colors[2] / 255.0F, 0.2F));

        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
}
