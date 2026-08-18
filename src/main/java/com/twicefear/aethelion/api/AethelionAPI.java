package com.twicefear.aethelion.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

/**
 * Aethelion API - Library for custom animations and effects
 * This is a library plugin. Developers can use this API to:
 * 1. Play Blockbench animations on entities/players
 * 2. Apply bone scaling transformations
 * 3. Use custom particle effects from JSON definitions
 */
public interface AethelionAPI {
    
    // === Animation Methods ===
    
    /**
     * Play a Blockbench animation on an entity
     * @param entity The entity to animate
     * @param animationId The animation ID from bbmodel file
     * @return true if animation started successfully
     */
    boolean playAnimation(Entity entity, String animationId);
    
    /**
     * Play a Blockbench animation on an entity with options
     * @param entity The entity to animate
     * @param animationId The animation ID
     * @param loop Whether to loop the animation
     * @param speed Speed multiplier (1.0 = normal)
     * @return true if animation started successfully
     */
    boolean playAnimation(Entity entity, String animationId, boolean loop, float speed);
    
    /**
     * Stop a specific animation on an entity
     * @param entity The entity
     * @param animationId The animation to stop
     */
    void stopAnimation(Entity entity, String animationId);
    
    /**
     * Stop all animations on an entity
     * @param entity The entity
     */
    void stopAllAnimations(Entity entity);
    
    /**
     * Check if an entity is currently playing any animation
     * @param entity The entity to check
     * @return true if animating
     */
    boolean isAnimating(Entity entity);
    
    // === Bone Scaling Methods ===
    
    /**
     * Set scale for a specific bone on an entity
     * @param entity The entity
     * @param boneName Bone name (head, body, left_arm, etc.)
     * @param scale Scale factor (0.2 to 3.125)
     */
    void setBoneScale(Entity entity, String boneName, float scale);
    
    /**
     * Set non-uniform scale for a bone
     * @param entity The entity
     * @param boneName Bone name
     * @param scaleX X axis scale
     * @param scaleY Y axis scale
     * @param scaleZ Z axis scale
     */
    void setBoneScale(Entity entity, String boneName, float scaleX, float scaleY, float scaleZ);
    
    /**
     * Reset scale for a specific bone
     * @param entity The entity
     * @param boneName Bone name to reset
     */
    void resetBoneScale(Entity entity, String boneName);
    
    /**
     * Reset all bone scales on an entity
     * @param entity The entity
     */
    void resetAllBoneScales(Entity entity);
    
    /**
     * Get current scale of a bone
     * @param entity The entity
     * @param boneName Bone name
     * @return Current scale factor
     */
    float getBoneScale(Entity entity, String boneName);
    
    // === Custom Particle Effects ===
    
    /**
     * Load a custom particle effect from JSON definition
     * @param effectId Unique identifier for the effect
     * @param jsonPath Path to JSON file in plugin folder
     * @return true if loaded successfully
     */
    boolean loadCustomEffect(String effectId, String jsonPath);
    
    /**
     * Play a custom particle effect at location
     * @param effectId The effect ID
     * @param location World location
     * @param viewers Players who will see the effect
     */
    void playCustomEffect(String effectId, org.bukkit.Location location, List<Player> viewers);
    
    /**
     * Play a custom particle effect on an entity
     * @param effectId The effect ID
     * @param entity Entity to attach effect to
     */
    void playCustomEffectOnEntity(String effectId, Entity entity);
    
    /**
     * Stop a custom effect playing on an entity
     * @param effectId The effect ID
     * @param entity The entity
     */
    void stopCustomEffect(String effectId, Entity entity);
    
    /**
     * Register a texture path for custom particles
     * @param textureId Texture identifier
     * @param texturePath Path to texture file
     */
    void registerTexture(String textureId, String texturePath);
    
    // === Utility Methods ===
    
    /**
     * Get list of available animations
     * @return List of animation IDs
     */
    List<String> getAvailableAnimations();
    
    /**
     * Get list of loaded custom effects
     * @return List of effect IDs
     */
    List<String> getLoadedEffects();
    
    /**
     * Reload all animations and effects
     */
    void reloadAll();
    
    /**
     * Check if resource pack is enabled
     * @return true if enabled
     */
    boolean isResourcePackEnabled();
}
