package com.yourname.emoteengine;

import com.yourname.emoteengine.api.EmoteAPI;
import com.yourname.emoteengine.engine.AnimationEngine;
import com.yourname.emoteengine.engine.ScaleModifier;
import com.yourname.emoteengine.listener.EmoteCommand;
import com.yourname.emoteengine.listener.JoinListener;
import com.yourname.emoteengine.model.ModelLoader;
import com.yourname.emoteengine.renderer.ResourcePackGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class EmoteEngine extends JavaPlugin implements EmoteAPI {
    private static EmoteEngine instance;
    private AnimationEngine animationEngine;
    private ScaleModifier scaleModifier;
    private ModelLoader modelLoader;
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
        this.modelLoader = new ModelLoader(this);
        this.animationEngine = new AnimationEngine(this);
        this.packGenerator = new ResourcePackGenerator(this);
        
        // Register commands
        Objects.requireNonNull(getCommand("emote")).setExecutor(new EmoteCommand(this));
        Objects.requireNonNull(getCommand("emote")).setTabCompleter(new EmoteCommand(this));
        
        // Register events
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        
        // Schedule animation tick
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, 
            animationEngine::tick, 0L, 1L);
        
        // Generate resource pack if enabled
        if (getConfig().getBoolean("emote-engine.resource-pack.enabled")) {
            try {
                packGenerator.generatePack();
                getLogger().info("Resource pack generated successfully!");
            } catch (Exception e) {
                getLogger().warning("Failed to generate resource pack: " + e.getMessage());
            }
        }
        
        getLogger().info("EmoteEngine v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Running on " + Bukkit.getServer().getVersion());
    }
    
    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            animationEngine.stopAllAnimations(p);
        }
        animationEngine.cleanup();
        getLogger().info("EmoteEngine disabled");
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
    
    // === API Implementation ===
    
    @Override
    public boolean playEmote(Player player, String emoteId) {
        return animationEngine.playAnimation(player, emoteId);
    }
    
    @Override
    public void stopEmote(Player player, String emoteId) {
        animationEngine.stopAnimation(player, emoteId);
    }
    
    @Override
    public void stopAllEmotes(Player player) {
        animationEngine.stopAllAnimations(player);
    }
    
    @Override
    public boolean isEmoting(Player player) {
        return animationEngine.isEmoting(player);
    }
    
    @Override
    public void setScale(Player player, String boneName, float scale) {
        scaleModifier.applyBoneScale(player, boneName, scale);
    }
    
    @Override
    public void resetScale(Player player, String boneName) {
        if (boneName == null || boneName.isEmpty()) {
            String[] bones = {"head", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
            for (String bone : bones) {
                scaleModifier.resetBoneScale(player, bone);
            }
        } else {
            scaleModifier.resetBoneScale(player, boneName);
        }
    }
    
    @Override
    public float getScale(Player player, String boneName) {
        return scaleModifier.getBoneScale(player, boneName);
    }
    
    @Override
    public void promptResourcePack(Player player) {
        packGenerator.promptPlayer(player);
    }
    
    @Override
    public boolean hasResourcePack(Player player) {
        return true; // Simplified
    }
    
    @Override
    public List<String> getAvailableEmotes() {
        return modelLoader.getAvailableAnimations();
    }
    
    @Override
    public void reloadAnimations() {
        modelLoader.reloadCache();
        try {
            packGenerator.generatePack();
            getLogger().info("Resource pack regenerated!");
        } catch (Exception e) {
            getLogger().warning("Failed to regenerate resource pack: " + e.getMessage());
        }
        getLogger().info("Animations reloaded!");
    }
    
    // === Getters ===
    
    public static EmoteEngine getInstance() {
        return instance;
    }
    
    public AnimationEngine getAnimationEngine() {
        return animationEngine;
    }
    
    public ScaleModifier getScaleModifier() {
        return scaleModifier;
    }
    
    public ModelLoader getModelLoader() {
        return modelLoader;
    }
}
