package com.twicefear.aethelion.api;

import org.bukkit.entity.Entity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks animation state for a single entity
 */
public class AnimatedEntity {
    private final UUID entityId;
    private final Map<String, AnimationState> activeAnimations = new ConcurrentHashMap<>();
    private final Map<String, Vector3f> boneScales = new ConcurrentHashMap<>();
    private long lastUpdate;
    
    public AnimatedEntity(Entity entity) {
        this.entityId = entity.getUniqueId();
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public UUID getEntityId() { return entityId; }
    
    public void addAnimation(AnimationState state) {
        activeAnimations.put(state.getId(), state);
    }
    
    public void removeAnimation(String id) {
        activeAnimations.remove(id);
    }
    
    public void clearAnimations() {
        activeAnimations.clear();
    }
    
    public Collection<AnimationState> getActiveAnimations() {
        return activeAnimations.values();
    }
    
    public void setBoneScale(String boneName, float scaleX, float scaleY, float scaleZ) {
        boneScales.put(boneName, new Vector3f(scaleX, scaleY, scaleZ));
    }
    
    public Vector3f getBoneScale(String boneName) {
        return boneScales.getOrDefault(boneName, new Vector3f(1.0f, 1.0f, 1.0f));
    }
    
    public void resetBoneScale(String boneName) {
        boneScales.remove(boneName);
    }
    
    public void clearBoneScales() {
        boneScales.clear();
    }
    
    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate >= 50) {
            for (AnimationState state : activeAnimations.values()) {
                state.advance();
            }
            lastUpdate = now;
        }
    }
}

/**
 * Runtime state of a playing animation
 */
class AnimationState {
    private final String id;
    private final AnimationData data;
    private int currentTick = 0;
    private boolean finished = false;
    private final float speedMultiplier;
    
    public AnimationState(AnimationData data) {
        this(data, 1.0f);
    }
    
    public AnimationState(AnimationData data, float speedMultiplier) {
        this.id = data.getId();
        this.data = data;
        this.speedMultiplier = speedMultiplier;
    }
    
    public String getId() { return id; }
    public AnimationData getData() { return data; }
    public int getCurrentTick() { return currentTick; }
    public boolean isFinished() { return finished; }
    public float getSpeedMultiplier() { return speedMultiplier; }
    
    public void advance() {
        currentTick += speedMultiplier;
        if (currentTick >= data.getLength()) {
            if (data.isLoop()) {
                currentTick = 0;
            } else {
                finished = true;
            }
        }
    }
}
