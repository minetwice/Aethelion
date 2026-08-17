package com.twicefear.aethelion.api;

import org.joml.Vector3f;
import java.util.Map;

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
