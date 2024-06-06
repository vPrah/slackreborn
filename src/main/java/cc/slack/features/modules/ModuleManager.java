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
    public final Map<Class<? extends Module>, Module> modules = new LinkedHashMap<>();
    public final Map<Class<? extends Module>, Module> draggable = new LinkedHashMap<>();

    public void initialize() {
        try {
            addModules(
                    // Combat
                    new KillAura(),
                    new Velocity(),
                    new Criticals(),
                    new Hitbox(),
                    new AntiFireball(),
                    new TickBase(),

                    // Movement
                    new Flight(),
                    new Speed(),
                    new NoSlow(),
                    new SafeWalk(),
                    new InvMove(),
                    new LongJump(),
                    new Step(),
                    new Sprint(),
                    new Strafe(),
                    new Jesus(),
                    new NoWeb(),

                    // Other
                    new AntiBot(),
                    new Performance(),
                    new RemoveEffect(),
                    new RichPresence(),
                    new LightningDetector(),
                    new Killsults(),
                    new Targets(),
                    new Tweaks(),

                    // Player
                    new AntiVoid(),
                    new NoFall(),
                    new Blink(),
                    new TestBlink(),
                    new FastEat(),
                    new FreeLook(),
                    new FreeCam(),
                    new TimerModule(),

                    // World
                    new Scaffold(),
                    new Stealer(),
                    new InvManager(),
                    new Breaker(),
                    new SpeedMine(),
                    new FastPlace(),

                    // Exploit
                    new Disabler(),
                    new Regen(),
                    new FastBow(),
                    new Phase(),
                    new ChatBypass(),
                    new ClientSpoofer(),
                    new Kick(),
                    new MultiAction(),

                    // Render
                    new HUD(),
                    new ClickGUI(),
                    new ESP(),
                    new NameTags(),
                    new Animations(),
                    new TargetHUD(),
                    new KeyStrokes(),
                    new Ambience(),
                    new BlockOverlay(),
                    new Cape(),
                    new ChinaHat(),
                    new Chams(),
                    new ChestESP(),
                    new ItemPhysics(),
                    new Spider(),
                    new Bobbing(),
                    new Tracers(),
                    new Projectiles(),
                    new ScoreboardModule(),
                    new XRay(),

                    // Ghost
                    new AimBot(),
                    new Autoclicker(),
                    new Reach(),
                    new LegitScaffold(),
                    new JumpReset(),
                    new Wtap(),

                    new AimAssist(),
                    new AutoSword(),
                    new AutoRod(),
                    new AutoTool(),
                    new Backtrack(),
                    new KeepSprint(),
                    new LegitNofall(),
                    new PearlAntiVoid(),
                    new Stap(),

                    // Utilities
                    new AutoDisable(),
                    new AutoGapple(),
                    new AutoGG(),
                    new AutoConfig(),
                    new AutoPlay(),
                    new AutoRespawn(),
                    new AutoLogin(),
                    new AutoPot(),
                    new AntiStaff(),
                    new FakePlayer(),
                    new NameProtect(),
                    new HealthWarn(),
                    new LegitMode(),
                    new LagbackChecker(),
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
