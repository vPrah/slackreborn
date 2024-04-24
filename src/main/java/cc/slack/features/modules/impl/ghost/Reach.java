package cc.slack.features.modules.impl.ghost;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.ModuleInfo;
import cc.slack.features.modules.api.settings.impl.NumberValue;
import net.minecraft.util.MathHelper;

import java.util.Random;

@ModuleInfo(
        name = "Reach",
        category = Category.GHOST
)
public class Reach extends Module {

    public final NumberValue<Double> reach = new NumberValue<>("Reach", 3.1D, 3D, 6D, 0.01D);
    public final NumberValue<Double> chance = new NumberValue<>("Chance", 1D, 0D, 1D, 0.01D);

    public Reach() {
        super();
        addSettings(reach, chance);
    }

    public double getReach() {
        double rnd = MathHelper.getRandomDoubleInRange(new Random(), 0, 1);
        if (rnd > chance.getValue()) {
            return reach.getValue();
        }
        return 3.0D;
    }

}
