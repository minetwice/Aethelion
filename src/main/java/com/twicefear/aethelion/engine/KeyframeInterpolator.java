package com.twicefear.aethelion.engine;

import com.twicefear.aethelion.api.BoneKeyframe;
import org.joml.Vector3f;

public class KeyframeInterpolator {

    public BonePose interpolate(BoneKeyframe prev, BoneKeyframe next, float progress) {
        // Linear interpolation with scale support
        Vector3f pos = new Vector3f(
            prev.getPosition().x() + (next.getPosition().x() - prev.getPosition().x()) * progress,
            prev.getPosition().y() + (next.getPosition().y() - prev.getPosition().y()) * progress,
            prev.getPosition().z() + (next.getPosition().z() - prev.getPosition().z()) * progress
        );

        Vector3f rot = new Vector3f(
            prev.getRotation().x() + (next.getRotation().x() - prev.getRotation().x()) * progress,
            prev.getRotation().y() + (next.getRotation().y() - prev.getRotation().y()) * progress,
            prev.getRotation().z() + (next.getRotation().z() - prev.getRotation().z()) * progress
        );

        Vector3f scale = new Vector3f(
            prev.getScale().x() + (next.getScale().x() - prev.getScale().x()) * progress,
            prev.getScale().y() + (next.getScale().y() - prev.getScale().y()) * progress,
            prev.getScale().z() + (next.getScale().z() - prev.getScale().z()) * progress
        );

        return new BonePose(pos, rot, scale);
    }
}
