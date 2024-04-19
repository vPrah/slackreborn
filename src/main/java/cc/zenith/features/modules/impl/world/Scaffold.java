package cc.zenith.features.modules.impl.world;

import cc.zenith.events.State;
import cc.zenith.events.impl.game.TickEvent;
import cc.zenith.events.impl.player.MotionEvent;
import cc.zenith.features.modules.api.Category;
import cc.zenith.features.modules.api.Module;
import cc.zenith.features.modules.api.ModuleInfo;
import cc.zenith.utils.client.mc;
import cc.zenith.utils.player.RotationUtil;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;


@ModuleInfo(
        name = "Scaffold",
        category = Category.WORLD,
        key = Keyboard.KEY_G
)
public class Scaffold extends Module {

    float yaw;

    @Override
    public void onEnable() {

    }

    @Listen
    public void onMotion(MotionEvent event) {
        yaw = mc.getPlayer().rotationYaw + 180;
        event.setYaw(yaw);
        event.setPitch(81.5f);
    }

    @Listen
    public void onTick(TickEvent event) {
        if (event.getState() != State.PRE) return;
        BlockPos below = new BlockPos(mc.getPlayer().posX, mc.getPlayer().posY - 1, mc.getPlayer().posZ);
        if(mc.getWorld().getBlockState(below).getBlock().getMaterial() == Material.air) return;
        EnumFacing facing = RotationUtil.getEnumDirection(yaw);

        mc.getPlayerController().onPlayerRightClick(mc.getPlayer(), mc.getWorld(), mc.getPlayer().getHeldItem(), below, EnumFacing.WEST, new Vec3(0.5, 0.5, 0.5));
    }
}