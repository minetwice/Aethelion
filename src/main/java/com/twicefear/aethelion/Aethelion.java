package com.twicefear.aethelion;

import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.api.EmoteAPI;
import com.twicefear.aethelion.engine.AnimationEngine;
import com.twicefear.aethelion.engine.ScaleModifier;
import com.twicefear.aethelion.listener.EmoteCommand;
import com.twicefear.aethelion.listener.JoinListener;
import com.twicefear.aethelion.model.ModelLoader;
import com.twicefear.aethelion.renderer.ResourcePackGenerator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class Aethelion extends JavaPlugin implements EmoteAPI {
    private static Aethelion instance;
    private AnimationEngine animationEngine;
    private ScaleModifier scaleModifier;
    private ModelLoader modelLoader;
    private ResourcePackGenerator packGenerator;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        // Validate Paper version
        validateServerVersion();

        // Initialize components
        this.scaleModifier = new ScaleModifier(this);
        this.modelLoader = new ModelLoader(this);
        this.animationEngine = new AnimationEngine(this);
        this.packGenerator = new ResourcePackGenerator(this);

        // Register commands
        Objects.requireNonNull(getCommand("emote")).setExecutor(new EmoteCommand(this));

        // Register events
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        // Schedule animation tick (20 ticks per second)
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

        getLogger().info("Aethelion v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Running on " + Bukkit.getServer().getVersion());
        getLogger().info("Paper: " + isPaper() + " | Folia: " + isFolia());
    }

    @Override
    public void onDisable() {
        // Clean up all animations
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (animationEngine != null) {
                animationEngine.stopAllAnimations(p);
            }
        }
        if (animationEngine != null) {
            animationEngine.cleanup();
        }
        getLogger().info("Aethelion disabled");
    }

    private void validateServerVersion() {
        String version = Bukkit.getBukkitVersion();
        getLogger().info("Server version: " + version);

        // Check if running on supported version
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
            // Reset all bones
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
    public boolean registerAnimation(String id, String jsonContent) {
        return modelLoader.registerAnimation(id, jsonContent);
    }

    @Override
    public boolean registerAnimation(String id, InputStream inputStream) {
        return modelLoader.registerAnimation(id, inputStream);
    }

    @Override
    public boolean registerAnimation(AnimationData animationData) {
        return modelLoader.registerAnimation(animationData);
    }

    @Override
    public void promptResourcePack(Player player) {
        packGenerator.promptPlayer(player);
    }

    @Override
    public boolean hasResourcePack(Player player) {
        // Simplified - in production check player's resource pack status
        return true;
    }

    @Override
    public List<String> getAvailableEmotes() {
        return modelLoader.getAvailableAnimations();
    }

    @Override
    public void reloadAnimations() {
        modelLoader.reloadCache();
        // Regenerate resource pack
        try {
            packGenerator.generatePack();
            getLogger().info("Resource pack regenerated!");
        } catch (Exception e) {
            getLogger().warning("Failed to regenerate resource pack: " + e.getMessage());
        }
        getLogger().info("Animations reloaded!");
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

    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    public ResourcePackGenerator getPackGenerator() {
        return packGenerator;
    }
}
