package com.twicefear.aethelion.engine;

import com.twicefear.aethelion.api.AnimatedEntity;
import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.loader.AnimationLoader;
import com.twicefear.aethelion.renderer.EntityRenderer;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.entity.Entity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core animation engine that manages entity animations
 */
public class AnimationEngine {
    private final Map<UUID, AnimatedEntity> activeEntities = new ConcurrentHashMap<>();
    private final AnimationLoader animationLoader;
    private final EntityRenderer renderer;
    private final KeyframeInterpolator interpolator;
    private final Aethelion plugin;
    
    public AnimationEngine(Aethelion plugin) {
        this.plugin = plugin;
        this.animationLoader = new AnimationLoader(plugin);
        this.renderer = new EntityRenderer(plugin);
        this.interpolator = new KeyframeInterpolator();
    }
    
    public boolean playAnimation(Entity entity, String animationId) {
        return playAnimation(entity, animationId, true, 1.0f);
    }
    
    public boolean playAnimation(Entity entity, String animationId, boolean loop, float speed) {
        AnimationData anim = animationLoader.loadAnimation(animationId);
        if (anim == null) return false;
        
        AnimatedEntity ae = activeEntities.computeIfAbsent(entity.getUniqueId(), 
            uuid -> new AnimatedEntity(entity));
        
        // Override loop setting if specified
        AnimationData overriddenAnim = new AnimationData(
            anim.getId(), 
            anim.getBoneAnimations(), 
            anim.getLength(), 
            loop
        );
        
        com.twicefear.aethelion.api.AnimationState state = 
            new com.twicefear.aethelion.api.AnimationState(overriddenAnim, speed);
        ae.addAnimation(state);
        
        renderer.sendAnimationUpdate(entity, ae);
        return true;
    }
    
    public void stopAnimation(Entity entity, String animationId) {
        AnimatedEntity ae = activeEntities.get(entity.getUniqueId());
        if (ae != null) {
            ae.removeAnimation(animationId);
        }
    }
    
    public void stopAllAnimations(Entity entity) {
        AnimatedEntity ae = activeEntities.get(entity.getUniqueId());
        if (ae != null) {
            ae.clearAnimations();
            renderer.resetEntity(entity);
        }
        activeEntities.remove(entity.getUniqueId());
    }
    
    public void tick() {
        for (AnimatedEntity ae : activeEntities.values()) {
            if (ae.getActiveAnimations().isEmpty()) continue;
            
            ae.update();
            
            // Check if entity is still valid
            Entity entity = plugin.getServer().getEntity(ae.getEntityId());
            if (entity != null && entity.isValid()) {
                renderer.sendAnimationUpdate(entity, ae);
            }
        }
    }
    
    public boolean isAnimating(Entity entity) {
        AnimatedEntity ae = activeEntities.get(entity.getUniqueId());
        return ae != null && !ae.getActiveAnimations().isEmpty();
    }
    
    public void cleanup() {
        activeEntities.clear();
    }
    
    public AnimationLoader getAnimationLoader() {
        return animationLoader;
    }
}
