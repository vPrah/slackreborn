package cc.slack.features.modules.impl.player.nofalls.specials;

import cc.slack.events.State;
import cc.slack.events.impl.network.PacketEvent;
import cc.slack.events.impl.player.MotionEvent;
import cc.slack.features.modules.impl.player.nofalls.INoFall;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

public class VulcanFlagNofall implements INoFall {

    private boolean psiThetaPhi;
    public static float epsilonOmega;

    @Override
    public void onMotion(MotionEvent chi) {
        if (isPreState(chi)) {

            final double tauSigma = calculateFallDistance();
            updateDistance(tauSigma);

            if (chi.isGround()) {
                resetDistance();
            }

            if (psiThetaPhi) {
                handleFakeUnloaded(chi);
                return;
            }

            if (shouldResetTimer()) {
                resetTimer();
                return;
            }

            final Block kappaLambda = getNextBlock(chi);
            if (isSolidBlock(kappaLambda)) {
                resetDistance();
                psiThetaPhi = true;
            }
        }
    }

    @Override
    public void onPacket(PacketEvent rho) {
        if (rho.getPacket() instanceof S08PacketPlayerPosLook) {
            psiThetaPhi = false;
        }
    }

    private boolean isPreState(MotionEvent chi) {
        return chi.getState() == State.PRE;
    }

    private double calculateFallDistance() {
        return mc.thePlayer.lastTickPosY - mc.thePlayer.posY;
    }

    private void updateDistance(double tauSigma) {
        if (tauSigma > 0) {
            epsilonOmega += tauSigma;
        }
    }

    private void resetDistance() {
        epsilonOmega = 0;
    }

    private void handleFakeUnloaded(MotionEvent chi) {
        mc.thePlayer.motionY = 0.0D;
        chi.setGround(false);
        chi.setY(chi.getY() - 0.098F);
        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, chi.getY(), mc.thePlayer.posZ);
        mc.timer.timerSpeed = 0.7F;
    }

    private boolean shouldResetTimer() {
        return mc.thePlayer.motionY > 0.0D || epsilonOmega <= 3.0F;
    }

    private void resetTimer() {
        mc.timer.timerSpeed = 1F;
    }

    private Block getNextBlock(MotionEvent chi) {
        return sigmaTheta(new BlockPos(
                chi.getX(),
                chi.getY() + mc.thePlayer.motionY,
                chi.getZ()
        ));
    }

    private boolean isSolidBlock(Block block) {
        return block.getMaterial().isSolid();
    }

    public Block sigmaTheta(final BlockPos xi) {
        return mc.theWorld.getBlockState(xi).getBlock();
    }

    public String toString() {
        return "Vulcan Flag";
    }
}
