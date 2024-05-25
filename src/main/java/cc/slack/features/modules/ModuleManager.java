// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.slack.features.modules.api.Category;
import cc.slack.features.modules.api.Module;
import cc.slack.features.modules.impl.combat.*;
import cc.slack.features.modules.impl.exploit.*;
import cc.slack.features.modules.impl.ghost.*;
import cc.slack.features.modules.impl.movement.*;
import cc.slack.features.modules.impl.other.*;
import cc.slack.features.modules.impl.player.*;
import cc.slack.features.modules.impl.render.*;
import cc.slack.features.modules.impl.utilties.*;
import cc.slack.features.modules.impl.world.*;

public class ModuleManager {
    private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();
    private final Map<Class<? extends Module>, Module> draggable = new LinkedHashMap<>();

    public void initialize() {
        try {
            addModules(
                    // Combat
                    new AntiFireball(),
                    new Criticals(),
                    new Hitbox(),
                    new KillAura(),
                    new TickBase(),
                    new Velocity(),

                    // Movement
                    new Flight(),
                    new InvMove(),
                    new Jesus(),
                    new NoSlow(),
                    new NoWeb(),
                    new SafeWalk(),
                    new Speed(),
                    new Sprint(),
                    new Step(),

                    // Other
                    new AntiBot(),
                    new Performance(),
                    new RemoveEffect(),
                    new RichPresence(),
                    new Killsults(),
                    new LightningDetector(),
                    new Targets(),
                    new Tweaks(),

                    // Player
                    new AntiVoid(),
                    new Blink(),
                    new FastEat(),
                    new FreeLook(),
                    new SpeedMine(),
                    new NoFall(),
                    new TestBlink(),
                    new TimerModule(),

                    // World
                    new Breaker(),
                    new FastPlace(),
                    new InvManager(),
                    new Scaffold(),
                    new Stealer(),

                    // Exploit
                    new ChatBypass(),
                    new Disabler(),
                    new Kick(),
                    new Regen(),
                    new FastBow(),
                    new MultiAction(),
                    new Phase(),

                    // Render
                    new Animations(),
                    new Ambience(),
                    new Bobbing(),
                    new BlockOverlay(),
                    new Cape(),
                    new ChinaHat(),
                    new Chams(),
                    new ChestESP(),
                    new ClickGUI(),
                    new ItemPhysics(),
                    new ESP(),
                    new HUD(),
                    new NameProtect(),
                    new Tracers(),
                    new FreeCam(),
                    new Projectiles(),
                    new ScoreboardModule(),
                    new TargetHUD(),
                    new XRay(),

                    // Ghost
                    new AimBot(),
                    new AimAssist(),
                    new Autoclicker(),
                    new AutoTool(),
                    new Backtrack(),
                    new JumpReset(),
                    new KeepSprint(),
                    new LegitNofall(),
                    new LegitScaffold(),
                    new PearlAntiVoid(),
                    new Reach(),
                    new Stap(),
                    new Wtap(),

                    // Utilities
                    new AutoGapple(),
                    new AutoSword(),
                    new AutoConfig(),
                    new AutoPlay(),
                    new AutoRespawn(),
                    new AutoLogin(),
                    new LegitMode(),
                    new PacketDebugger()

            );
            
    		for(Module m : modules.values()) {
    			draggable.put(m.getClass(), m);
    		}
    		
        } catch (Exception e) {
            // Shut Up Exception
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules.values());
    }
    
    public List<Module> getDraggable() {
    	return new ArrayList<>(draggable.values());
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
