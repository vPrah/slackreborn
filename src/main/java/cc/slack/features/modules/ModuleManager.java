// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules;

import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.api.Category;

import cc.slack.features.modules.impl.combat.*;
import cc.slack.features.modules.impl.exploit.*;
import cc.slack.features.modules.impl.ghost.*;
import cc.slack.features.modules.impl.movement.*;
import cc.slack.features.modules.impl.other.*;
import cc.slack.features.modules.impl.player.*;
import cc.slack.features.modules.impl.render.*;
import cc.slack.features.modules.impl.world.*;

import java.util.*;

public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();

    public void initialize() {
        try {
            addModules(
                    // Combat
                    new AntiFireball(),
                    new Criticals(),
                    new Hitbox(),
                    new KillAura(),
                    new Velocity(),

                    // Movement
                    new AirJump(),
                    new Flight(),
                    new InvMove(),
                    new Jesus(),
                    new NoSlow(),
                    new NoWeb(),
                    new Phase(),
                    new SafeWalk(),
                    new Sneak(),
                    new Speed(),
                    new Sprint(),
                    new Step(),

                    // Other
                    new Performance(),
                    new Tweaks(),

                    // Player
                    new AntiVoid(),
                    new AutoPlay(),
                    new AutoTool(),
                    new Blink(),
                    new FastEat(),
                    new NoFall(),
                    new TestBlink(),
                    new TimerModule(),

                    // World
                    new FastPlace(),
                    new InvManager(),
                    new Scaffold(),
                    new Stealer(),

                    // Exploit
                    new Disabler(),
                    new Kick(),
                    new MultiAction(),

                    // Render
                    new Ambience(),
                    new Bobbing(),
                    new ChestESP(),
                    new ClickGUI(),
                    new Projectiles(),
                    new ESP(),
                    new HUD(),
                    new TargetHUD(),

                    // Ghost
                    new Autoclicker(),
                    new LegitMode(),
                    new LegitAutoTool(),
                    new Backtrack(),
                    new JumpReset(),
                    new Reach(),
                    new Stap(),
                    new Wtap()

            );
        } catch (Exception e) {
            // Shut Up Exception
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }

    public <T extends Module> T getInstance(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public Module getModuleByName(String n) {
        for (Module m : modules.values()) {
            if (m.getName().equals(n))
                return m;
        }

        throw new IllegalArgumentException("Module not found.");
    }

    private void addModules(Module... mod) {
        for (Module m : mod)
            modules.put(m.getClass(), m);
    }

    public Module[] getModulesByCategory(Category c) {
        final List<Module> category = new ArrayList<>();

        modules.forEach((aClass, module) -> {
            if (module.getCategory() == c)
                category.add(module);
        });

        return category.toArray(new Module[0]);
    }

    public Module[] getModulesByCategoryABC(final Category c) {
        Module[] modulesByCategory = getModulesByCategory(c);
        Arrays.sort(modulesByCategory, Comparator.comparing(Module::getName));
        return modulesByCategory;
    }
}
