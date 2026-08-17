package com.twicefear.aethelion.api;

import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EmotePlayer {
    private final UUID playerId;
    private final Map<String, AnimationState> activeAnimations = new ConcurrentHashMap<>();
    private final Map<String, Float> boneScales = new ConcurrentHashMap<>();
    private long lastUpdate;

    public EmotePlayer(Player player) {
        this.playerId = player.getUniqueId();
        this.lastUpdate = System.currentTimeMillis();
    }

    public UUID getPlayerId() { return playerId; }

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

    public void setBoneScale(String boneName, float scale) {
        boneScales.put(boneName, scale);
    }

    public float getBoneScale(String boneName) {
        return boneScales.getOrDefault(boneName, 1.0f);
    }

    public void resetBoneScale(String boneName) {
        boneScales.remove(boneName);
    }

    public void update() {
        long now = System.currentTimeMillis();
        long delta = now - lastUpdate;
        if (delta >= 50) {
            for (AnimationState state : activeAnimations.values()) {
                state.advance();
            }
            lastUpdate = now;
        }
    }
}
