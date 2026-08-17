package com.twicefear.aethelion;

import com.twicefear.aethelion.api.AethelionAPI;
import com.twicefear.aethelion.engine.AnimationEngine;
import com.twicefear.aethelion.engine.ScaleModifier;
import com.twicefear.aethelion.listener.AethelionCommand;
import com.twicefear.aethelion.listener.PlayerListener;
import com.twicefear.aethelion.loader.AnimationLoader;
import com.twicefear.aethelion.particle.ParticleEffectLoader;
import com.twicefear.aethelion.particle.ParticleEffectPlayer;
import com.twicefear.aethelion.renderer.ResourcePackGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Aethelion - Library plugin for custom animations and particle effects
 * Author: twicefear
 * 
 * This is a LIBRARY plugin. It does not show emotes by itself.
 * Other plugins can use the API to:
 * - Play Blockbench animations on entities/players
 * - Apply bone scaling transformations  
 * - Use custom particle effects from JSON definitions
 */
public class Aethelion extends JavaPlugin implements AethelionAPI {
    private static Aethelion instance;
    private AnimationEngine animationEngine;
    private ScaleModifier scaleModifier;
    private ParticleEffectLoader particleLoader;
    private ParticleEffectPlayer particlePlayer;
    private ResourcePackGenerator packGenerator;
    
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        
        // Validate server version
        validateServerVersion();
        
        // Initialize components
        this.scaleModifier = new ScaleModifier(this);
        this.animationEngine = new AnimationEngine(this);
        this.particleLoader = new ParticleEffectLoader(this);
        this.particlePlayer = new ParticleEffectPlayer();
        this.packGenerator = new ResourcePackGenerator(this);
        
        // Register commands
        Objects.requireNonNull(getCommand("aethelion")).setExecutor(new AethelionCommand(this));
        Objects.requireNonNull(getCommand("aethelion")).setTabCompleter(new AethelionCommand(this));
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Schedule animation tick
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            animationEngine::tick, 0L, 1L);
        
        // Schedule particle effect tick
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            particlePlayer::tick, 0L, 1L);
        
        // Generate resource pack if enabled
        if (getConfig().getBoolean("aethelion.resource-pack.enabled")) {
            try {
                packGenerator.generatePack();
                getLogger().info("Resource pack generated successfully!");
            } catch (Exception e) {
                getLogger().warning("Failed to generate resource pack: " + e.getMessage());
            }
        }
        
        getLogger().info("Aethelion v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Running on " + Bukkit.getServer().getVersion());
        getLogger().info("Paper: " + isPaper() + " | Folia: " + isFolia());
        getLogger().info("This is a LIBRARY plugin for developers.");
    }
    
    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            animationEngine.stopAllAnimations(p);
            particlePlayer.stopAllOnEntity(p);
        }
        animationEngine.cleanup();
        particlePlayer.clearAll();
        getLogger().info("Aethelion disabled");
    }
    
    private void validateServerVersion() {
        String version = Bukkit.getBukkitVersion();
        getLogger().info("Server version: " + version);
        
        if (!version.contains("1.17") && !version.contains("1.18") && 
            !version.contains("1.19") && !version.contains("1.20") && 
            !version.contains("1.21")) {
            getLogger().warning("This plugin is designed for 1.17-1.21.11!");
            getLogger().warning("Some features may not work correctly.");
        }
    }
    
    private boolean isPaper() {
        try {
            Class.forName("io.papermc.paper.api.PaperAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    // === API Implementation ===
    
    @Override
    public boolean playAnimation(Entity entity, String animationId) {
        return animationEngine.playAnimation(entity, animationId);
    }
    
    @Override
    public boolean playAnimation(Entity entity, String animationId, boolean loop, float speed) {
        return animationEngine.playAnimation(entity, animationId, loop, speed);
    }
    
    @Override
    public void stopAnimation(Entity entity, String animationId) {
        animationEngine.stopAnimation(entity, animationId);
    }
    
    @Override
    public void stopAllAnimations(Entity entity) {
        animationEngine.stopAllAnimations(entity);
    }
    
    @Override
    public boolean isAnimating(Entity entity) {
        return animationEngine.isAnimating(entity);
    }
    
    @Override
    public void setBoneScale(Entity entity, String boneName, float scale) {
        scaleModifier.applyBoneScale(entity, boneName, scale);
    }
    
    @Override
    public void setBoneScale(Entity entity, String boneName, float scaleX, float scaleY, float scaleZ) {
        scaleModifier.applyBoneScale(entity, boneName, scaleX, scaleY, scaleZ);
    }
    
    @Override
    public void resetBoneScale(Entity entity, String boneName) {
        scaleModifier.resetBoneScale(entity, boneName);
    }
    
    @Override
    public void resetAllBoneScales(Entity entity) {
        scaleModifier.resetAllBoneScales(entity);
    }
    
    @Override
    public float getBoneScale(Entity entity, String boneName) {
        return scaleModifier.getBoneScaleUniform(entity, boneName);
    }
    
    @Override
    public boolean loadCustomEffect(String effectId, String jsonPath) {
        return particleLoader.loadEffect(effectId, jsonPath);
    }
    
    @Override
    public void playCustomEffect(String effectId, org.bukkit.Location location, List<Player> viewers) {
        particlePlayer.playEffect(effectId, location, viewers);
    }
    
    @Override
    public void playCustomEffectOnEntity(String effectId, Entity entity) {
        particlePlayer.playOnEntity(effectId, entity);
    }
    
    @Override
    public void stopCustomEffect(String effectId, Entity entity) {
        particlePlayer.stopOnEntity(effectId, entity);
    }
    
    @Override
    public void registerTexture(String textureId, String texturePath) {
        // Texture registration handled by resource pack generator
        getLogger().info("Texture registered: " + textureId + " -> " + texturePath);
    }
    
    @Override
    public List<String> getAvailableAnimations() {
        return animationEngine.getAnimationLoader().getAvailableAnimations();
    }
    
    @Override
    public List<String> getLoadedEffects() {
        return particleLoader.getLoadedEffects();
    }
    
    @Override
    public void reloadAll() {
        animationEngine.getAnimationLoader().reloadCache();
        particleLoader.clearAll();
        try {
            packGenerator.generatePack();
            getLogger().info("Resource pack regenerated!");
        } catch (Exception e) {
            getLogger().warning("Failed to regenerate resource pack: " + e.getMessage());
        }
        getLogger().info("Reloaded all animations and effects!");
    }
    
    @Override
    public boolean isResourcePackEnabled() {
        return getConfig().getBoolean("aethelion.resource-pack.enabled", false);
    }
    
    // === Getters for internal use ===
    
    public static Aethelion getInstance() {
        return instance;
    }
    
    public AnimationEngine getAnimationEngine() {
        return animationEngine;
    }
    
    public ScaleModifier getScaleModifier() {
        return scaleModifier;
    }
    
    public ParticleEffectLoader getParticleLoader() {
        return particleLoader;
    }
    
    public ParticleEffectPlayer getParticlePlayer() {
        return particlePlayer;
    }
    
    public AethelionAPI getAPI() {
        return this;
    }
}
