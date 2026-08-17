package com.yourname.emoteengine.renderer;

import com.yourname.emoteengine.api.EmotePlayer;
import com.yourname.emoteengine.api.AnimationState;
import com.yourname.emoteengine.engine.BonePose;
import com.yourname.emoteengine.engine.KeyframeInterpolator;
import com.yourname.emoteengine.EmoteEngine;
import org.bukkit.entity.Player;
import org.joml.Vector3f;

public class PacketBuilder {
    private final EmoteEngine plugin;
    private final KeyframeInterpolator interpolator;
    
    public PacketBuilder(EmoteEngine plugin) {
        this.plugin = plugin;
        this.interpolator = new KeyframeInterpolator();
    }
    
    public void sendAnimationUpdate(Player player, EmotePlayer emotePlayer) {
        // TODO: Implement packet sending logic for display entities or armor stands
        // This will be implemented in Stage 3 with the full renderer
        if (plugin.getConfig().getBoolean("emote-engine.debug", false)) {
            plugin.getLogger().info("Sending animation update for player: " + player.getName());
        }
    }
    
    public void resetPlayer(Player player) {
        // TODO: Implement reset logic to clear all animations
        if (plugin.getConfig().getBoolean("emote-engine.debug", false)) {
            plugin.getLogger().info("Resetting animations for player: " + player.getName());
        }
    }
    
    /**
     * Interpolates the current pose for a bone based on active animations
     */
    public BonePose calculateBonePose(EmotePlayer emotePlayer, String boneName) {
        Vector3f totalPos = new Vector3f(0, 0, 0);
        Vector3f totalRot = new Vector3f(0, 0, 0);
        Vector3f totalScale = new Vector3f(1, 1, 1);
        
        for (AnimationState state : emotePlayer.getActiveAnimations()) {
            if (state.isFinished()) continue;
            
            var boneKeyframes = state.getData().getBoneAnimations().get(boneName);
            if (boneKeyframes == null || boneKeyframes.length < 2) continue;
            
            int currentTick = state.getCurrentTick();
            
            // Find surrounding keyframes
            BoneKeyframe prev = null;
            BoneKeyframe next = null;
            
            for (int i = 0; i < boneKeyframes.length - 1; i++) {
                if (boneKeyframes[i].getTick() <= currentTick && boneKeyframes[i + 1].getTick() >= currentTick) {
                    prev = boneKeyframes[i];
                    next = boneKeyframes[i + 1];
                    break;
                }
            }
            
            if (prev != null && next != null) {
                float progress = (float)(currentTick - prev.getTick()) / (float)(next.getTick() - prev.getTick());
                BonePose pose = interpolator.interpolate(prev, next, progress);
                
                totalPos.add(pose.getPosition());
                totalRot.add(pose.getRotation());
                totalScale.mul(pose.getScale());
            }
        }
        
        // Apply manual bone scales
        float manualScale = emotePlayer.getBoneScale(boneName);
        totalScale.mul(new Vector3f(manualScale, manualScale, manualScale));
        
        return new BonePose(totalPos, totalRot, totalScale);
    }
}
