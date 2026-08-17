package com.twicefear.aethelion.api;

import org.joml.Vector3f;
import java.util.Map;

/**
 * Animation data parsed from Blockbench files
 */
public class AnimationData {
    private final String id;
    private final Map<String, BoneKeyframe[]> boneAnimations;
    private final int length;
    private final boolean loop;
    private final EasingType easing;
    
    public AnimationData(String id, Map<String, BoneKeyframe[]> boneAnimations, int length, boolean loop) {
        this.id = id;
        this.boneAnimations = boneAnimations;
        this.length = length;
        this.loop = loop;
        this.easing = EasingType.LINEAR;
    }
    
    public String getId() { return id; }
    public Map<String, BoneKeyframe[]> getBoneAnimations() { return boneAnimations; }
    public int getLength() { return length; }
    public boolean isLoop() { return loop; }
    public EasingType getEasing() { return easing; }
}

/**
 * Keyframe data for a single bone at a specific tick
 */
public class BoneKeyframe {
    private final int tick;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;
    
    public BoneKeyframe(int tick, Vector3f pos, Vector3f rot, Vector3f scale) {
        this.tick = tick;
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
    }
    
    public int getTick() { return tick; }
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getScale() { return scale; }
}

/**
 * Easing types for animation interpolation
 */
public enum EasingType {
    LINEAR, SMOOTH, STEP, EASE_IN, EASE_OUT, EASE_IN_OUT
}
