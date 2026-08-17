package com.twicefear.aethelion.particle;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages playing particle effects in real-time
 */
public class ParticleEffectPlayer {
    private final Map<String, ActiveEffect> activeEffects = new ConcurrentHashMap<>();
    private final Map<Entity, List<String>> entityEffects = new ConcurrentHashMap<>();
    
    /**
     * Play an effect at a location for specific viewers
     */
    public void playEffect(String effectId, Location location, List<Player> viewers) {
        ParticleEffect effect = Aethelion.getParticleLoader().getEffect(effectId);
        if (effect == null) return;
        
        ActiveEffect active = new ActiveEffect(effectId, effect, location, viewers);
        activeEffects.put(effectId + "_" + System.currentTimeMillis(), active);
        active.start();
    }
    
    /**
     * Play an effect attached to an entity
     */
    public void playOnEntity(String effectId, Entity entity) {
        ParticleEffect effect = Aethelion.getParticleLoader().getEffect(effectId);
        if (effect == null) return;
        
        // Stop existing effect with same ID on this entity
        stopOnEntity(effectId, entity);
        
        Location loc = entity.getLocation();
        List<Player> viewers = new ArrayList<>(entity.getWorld().getPlayers());
        
        ActiveEffect active = new ActiveEffect(effectId, effect, loc, viewers);
        String key = effectId + "_" + entity.getUniqueId();
        activeEffects.put(key, active);
        
        entityEffects.computeIfAbsent(entity, k -> new ArrayList<>()).add(key);
        active.start();
    }
    
    /**
     * Stop an effect on an entity
     */
    public void stopOnEntity(String effectId, Entity entity) {
        String key = effectId + "_" + entity.getUniqueId();
        ActiveEffect active = activeEffects.remove(key);
        if (active != null) {
            active.stop();
        }
        
        List<String> effects = entityEffects.get(entity);
        if (effects != null) {
            effects.remove(key);
            if (effects.isEmpty()) {
                entityEffects.remove(entity);
            }
        }
    }
    
    /**
     * Stop all effects on an entity
     */
    public void stopAllOnEntity(Entity entity) {
        List<String> effects = entityEffects.remove(entity);
        if (effects != null) {
            for (String key : effects) {
                ActiveEffect active = activeEffects.remove(key);
                if (active != null) {
                    active.stop();
                }
            }
        }
    }
    
    /**
     * Clean up finished effects
     */
    public void tick() {
        Iterator<Map.Entry<String, ActiveEffect>> it = activeEffects.entrySet().iterator();
        while (it.hasNext()) {
            ActiveEffect active = it.next().getValue();
            if (active.isFinished()) {
                active.stop();
                it.remove();
            } else {
                active.tick();
            }
        }
    }
    
    /**
     * Clear all active effects
     */
    public void clearAll() {
        for (ActiveEffect active : activeEffects.values()) {
            active.stop();
        }
        activeEffects.clear();
        entityEffects.clear();
    }
}

/**
 * Currently playing particle effect instance
 */
class ActiveEffect {
    private final String id;
    private final ParticleEffect effect;
    private Location location;
    private final List<Player> viewers;
    private int currentTick = 0;
    private boolean finished = false;
    private BukkitTask task;
    
    public ActiveEffect(String id, ParticleEffect effect, Location location, List<Player> viewers) {
        this.id = id;
        this.effect = effect;
        this.location = location;
        this.viewers = viewers;
    }
    
    public void start() {
        task = Aethelion.getInstance().getServer().getScheduler()
            .runTaskTimer(Aethelion.getInstance(), this::tick, 0L, 1L);
    }
    
    public void tick() {
        if (finished) return;
        
        // Update location if attached to entity
        // (handled externally for entity-attached effects)
        
        for (ParticleFrame frame : effect.getFrames()) {
            if (frame.getTick() == currentTick) {
                frame.spawn(location, viewers);
            }
        }
        
        currentTick++;
        if (currentTick >= effect.getDuration()) {
            if (effect.isLoop()) {
                currentTick = 0;
            } else {
                finished = true;
            }
        }
    }
    
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        finished = true;
    }
    
    public boolean isFinished() {
        return finished;
    }
}
