// Slack Client (discord.gg/slackclient)

package cc.slack.features.modules;

import java.util.ArrayList;
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
                    // DEV
                    new Strafe(),
                    new TargetStrafe(),
                    new MCF(),
                    new ClientSpoofer(),
                    new Radar(),
                    new AutoRod(),
                    new AimAssist(),
                    new AntiLava(),
                    new AntiCactus(),
                    new AntiHunger(),
                    new AntiAfk(),
                    new TNTHelper(),
                    new SessionInfo(),
                    new CustomESP(),
                    new BedESP(),

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
                    new VClip(),
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
                    new Kick(),
                    new MultiAction(),

                    // Render
                    new HUD(),
                    new ESP(),
                    new BasicESP(),
                    new ClickGUI(),
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
                    new Camera(),
                    new ItemPhysics(),
                    new Spider(),
                    new Bobbing(),
                    new Tracers(),
                    new Projectiles(),
                    new PointerESP(),
                    new ScoreboardModule(),
                    new XRay(),

                    // Ghost
                    new AimBot(),
                    new Autoclicker(),
                    new Reach(),
                    new LegitScaffold(),
                    new JumpReset(),
                    new Wtap(),
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
                    new AutoPlay(),
                    new AutoRespawn(),
                    new AutoLogin(),
                    new AutoPot(),
                    new AutoSword(),
                    new AntiStaff(),
                    new FakePlayer(),
                    new NameProtect(),
                    new HealthWarn(),
                    new LegitMode(),
                    new LagbackChecker()

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

    public Module getModuleByName(String name) {
        for (Module mod : modules.values()) {
            if (mod.getName() == name)
                return mod;
        }


        throw new IllegalArgumentException("Module not found.");
    }

    private void addModules(Module... mod) {
        for (Module m : mod) {

            modules.put(m.getClass(), m);
        }
    }

    public Module[] getModulesByCategory(Category cat) {
        ArrayList<Module> category = new ArrayList<>();

        modules.forEach((aClass, mod) -> {
            if (mod.category == cat)
                category.add(mod);
        });

        return category.toArray(new Module[0]);
    }
}
