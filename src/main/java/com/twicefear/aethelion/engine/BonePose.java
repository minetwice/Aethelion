package com.twicefear.aethelion.engine;

import org.joml.Vector3f;

public class BonePose {
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
