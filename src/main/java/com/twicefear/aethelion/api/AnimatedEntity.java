package com.twicefear.aethelion.api;

import org.bukkit.entity.Entity;
import org.joml.Vector3f;
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
