package com.yourname.emoteengine.api;

import org.joml.Vector3f;

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
