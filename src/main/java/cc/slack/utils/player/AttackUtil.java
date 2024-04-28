package cc.slack.utils.player;

import cc.slack.utils.other.TimeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import java.security.SecureRandom;
import java.util.Random;

public class AttackUtil {

    // impl in playercontrollermp and entityplayersp
    public static boolean inCombat = false;
    public static Entity combatTarget = null;
    public static final TimeUtil combatTimer = new TimeUtil();
    public static final long combatDuration = 600L;


    public enum AttackPattern {
        OLD, NEW, EXTRA
    }

    private static double devRand1 = 0f;
    private static double devRand2 = 0f;
    private static double devRand3 = 0f;
    private static double devRand4 = 0f;
    private static int devRandTick = 0;

    public static long getAttackDelay(int cps, double rand, AttackPattern pattern) {
        switch (pattern) {
            case OLD:
                return (long) (1000 / getOldRandomization(cps, rand));
            case NEW:
                return (long) (1000 / getNewRandomization(cps, rand));
            case EXTRA:
                return (long) (1000 / getExtraRandomization(cps, rand));
        }
        return 0;
    }

    public static double getOldRandomization(double cps, double sigma) {
        double rnd = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
        return cps + sigma * normal;
    }

    public static double getNewRandomization(double cps, double rand) {
        double rnd = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
        return cps + rand * normal + ((cps + new SecureRandom().nextDouble() * rand) / 4);
    }

    public static double getExtraRandomization(double cps, double rand) {
        // choose new focus points every 30 clicks
        if (devRandTick % 30 == 0) {
            devRand1 = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
            devRand2 = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
            devRand3 = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
            devRand4 = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        }

        // choose two of the focus points to get randomization

        double randOffset1 = 0D;
        double randOffset2 = 0D;

        switch (MathHelper.getRandomIntegerInRange(new Random(), 1, 4)) {
            case 1:
                randOffset1 = devRand1;
                break;
            case 2:
                randOffset1 = devRand2;
                break;
            case 3:
                randOffset1 = devRand3;
                break;
            case 4:
                randOffset1 = devRand4;
                break;
        }
        switch (MathHelper.getRandomIntegerInRange(new Random(), 1, 4)) {
            case 1:
                randOffset2 = devRand1;
                break;
            case 2:
                randOffset2 = devRand2;
                break;
            case 3:
                randOffset2 = devRand3;
                break;
            case 4:
                randOffset2 = devRand4;
                break;
        }

        // get randomization on both focus, shifting target cps and random amount
        // average the two, focusing on rand1

        double rand1 = getNewRandomization(cps + ((-0.3 + randOffset1 * 0.6) * rand), rand * (0.5 + (randOffset1 * 0.3)));
        double rand2 = getOldRandomization(cps + (randOffset2 * rand), rand * (0.2 + (randOffset2 * 0.4)));
        return (3 * rand1 + rand2) / 4;
    }
}
