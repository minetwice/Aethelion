package com.yourname.emoteengine.api;

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
        if (now - lastUpdate >= 50) {
            for (AnimationState state : activeAnimations.values()) {
                state.advance();
            }
            lastUpdate = now;
        }
    }
}

public class AnimationState {
    private final String id;
    private final AnimationData data;
    private int currentTick = 0;
    private boolean finished = false;
    
    public AnimationState(AnimationData data) {
        this.id = data.getId();
        this.data = data;
    }
    
    public String getId() { return id; }
    public AnimationData getData() { return data; }
    public int getCurrentTick() { return currentTick; }
    public boolean isFinished() { return finished; }
    
    public void advance() {
        currentTick++;
        if (currentTick >= data.getLength()) {
            if (data.isLoop()) {
                currentTick = 0;
            } else {
                finished = true;
            }
        }
    }
}
