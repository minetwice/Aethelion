package com.twicefear.aethelion.particle;

import com.twicefear.aethelion.Aethelion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCodeParticleRuntime {
    private final Aethelion plugin;
    private final Map<String, CustomCodeParticleEffect> customCodeEffects = new ConcurrentHashMap<>();

    public CustomCodeParticleRuntime(Aethelion plugin) {
        this.plugin = plugin;
    }

    @FunctionalInterface
    public interface ParticleStepCallback {
        void onStep(Location origin, int currentTick, CustomCodeParticleEffect effect);
    }

    public static class CustomCodeParticleEffect {
        private final String id;
        private final ParticleStepCallback stepCallback;
        private final int durationTicks;

        public CustomCodeParticleEffect(String id, ParticleStepCallback stepCallback, int durationTicks) {
            this.id = id;
            this.stepCallback = stepCallback;
            this.durationTicks = durationTicks;
        }

        public String getId() { return id; }
        public ParticleStepCallback getStepCallback() { return stepCallback; }
        public int getDurationTicks() { return durationTicks; }
    }

    public boolean registerCustomParticleEffect(String id, int durationTicks, ParticleStepCallback callback) {
        if (id == null || callback == null) return false;
        customCodeEffects.put(id, new CustomCodeParticleEffect(id, callback, durationTicks));
        plugin.getLogger().info("Registered custom code particle effect: " + id);
        return true;
    }

    public boolean runParticleEffect(Location location, String effectId) {
        CustomCodeParticleEffect effect = customCodeEffects.get(effectId);
        if (effect == null || location == null) return false;

        new BukkitRunnable() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick >= effect.getDurationTicks() || location.getWorld() == null) {
                    cancel();
                    return;
                }
                effect.getStepCallback().onStep(location.clone(), tick, effect);
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        return true;
    }

    public boolean runParticleEffectOnPlayer(Player player, String effectId) {
        if (player == null) return false;
        return runParticleEffect(player.getLocation().add(0, 1.0, 0), effectId);
    }
}
