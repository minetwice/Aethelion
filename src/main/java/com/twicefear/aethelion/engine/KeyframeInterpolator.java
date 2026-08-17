package com.twicefear.aethelion.engine;

import org.joml.Vector3f;

/**
 * Interpolates between bone keyframes
 */
public class KeyframeInterpolator {
    
    public BonePose interpolate(BoneKeyframe prev, BoneKeyframe next, float progress) {
        // Linear interpolation with scale support
        Vector3f pos = new Vector3f(
            lerp(prev.getPosition().x(), next.getPosition().x(), progress),
            lerp(prev.getPosition().y(), next.getPosition().y(), progress),
            lerp(prev.getPosition().z(), next.getPosition().z(), progress)
        );
        
        Vector3f rot = new Vector3f(
            lerp(prev.getRotation().x(), next.getRotation().x(), progress),
            lerp(prev.getRotation().y(), next.getRotation().y(), progress),
            lerp(prev.getRotation().z(), next.getRotation().z(), progress)
        );
        
        Vector3f scale = new Vector3f(
            lerp(prev.getScale().x(), next.getScale().x(), progress),
            lerp(prev.getScale().y(), next.getScale().y(), progress),
            lerp(prev.getScale().z(), next.getScale().z(), progress)
        );
        
        return new BonePose(pos, rot, scale);
    }
    
    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}

/**
 * Transformed bone pose at a specific point in time
 */
class BonePose {
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;
    
    public BonePose(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getScale() { return scale; }
}
