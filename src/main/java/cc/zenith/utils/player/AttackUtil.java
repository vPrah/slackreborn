package cc.zenith.utils.player;

import net.minecraft.util.MathHelper;

import java.security.SecureRandom;
import java.util.Random;

public class AttackUtil {

    public enum AttackPattern {
        OLD, NEW
    }

    public static long getAttackDelay(int cps, double rand, AttackPattern pattern) {
        switch (pattern) {
            case OLD:
                return (long) (1000 / getOldRandomization(cps, rand));
            case NEW:
                return (long) (1000 / getNewRandomization(cps, rand));
        }
        return 0;
    }

    public static double getOldRandomization(double mu, double sigma) {
        double rnd = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
        return mu + sigma * normal;
    }

    public static double getNewRandomization(double cps, double rand) {
        double rnd = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        double normal = Math.sqrt(-2 * (Math.log(rnd) / Math.log(Math.E))) * Math.sin(2 * Math.PI * rnd);
        return cps + rand * normal + ((cps + new SecureRandom().nextDouble() * rand) / 4);
    }
}
