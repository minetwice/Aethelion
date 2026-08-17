package com.twicefear.aethelion.api;

import com.twicefear.aethelion.model.UniversalModelLoader;
import com.twicefear.aethelion.particle.CustomCodeParticleRuntime;
import com.twicefear.aethelion.particle.CustomParticleData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.io.InputStream;
import java.util.List;

public interface EmoteAPI {
    // Core emote methods
    boolean playEmote(Player player, String emoteId);
    void stopEmote(Player player, String emoteId);
    void stopAllEmotes(Player player);
    boolean isEmoting(Player player);

    // Scale methods (YOUR SPECIAL FEATURE)
    void setScale(Player player, String boneName, float scale);
    void resetScale(Player player, String boneName);
    float getScale(Player player, String boneName);

    // External Animation & Universal Model Registration (Developer Hooks - No Resource Pack Needed)
    boolean registerAnimation(String id, String jsonContent);
    boolean registerAnimation(String id, InputStream inputStream);
    boolean registerAnimation(AnimationData animationData);
    boolean registerUniversalObjModel(String id, InputStream objStream, InputStream mtlStream);
    UniversalModelLoader.UniversalModelData getUniversalModel(String id);

    // Custom Particle Methods (JSON & Procedural Code - Without Resource Pack)
    boolean registerParticle(String id, String jsonContent);
    boolean registerParticle(String id, InputStream inputStream);
    boolean spawnParticle(Location location, String particleId);
    boolean spawnParticle(Location location, CustomParticleData particleData);
    boolean spawnParticleOnPlayer(Player player, String particleId);
    List<String> getAvailableParticles();

    // Procedural Code Particle Engine (No Resource Pack)
    boolean registerCustomCodeParticleEffect(String id, int durationTicks, CustomCodeParticleRuntime.ParticleStepCallback callback);
    boolean runParticleEffect(Location location, String effectId);
    boolean runParticleEffectOnPlayer(Player player, String effectId);

    // Resource pack methods
    void promptResourcePack(Player player);
    boolean hasResourcePack(Player player);

    // Utility methods
    List<String> getAvailableEmotes();
    void reloadAnimations();
}
