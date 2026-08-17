package com.twicefear.aethelion.engine;

import com.twicefear.aethelion.Aethelion;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.joml.Vector3f;

/**
 * Manages bone scaling for entities
 */
public class ScaleModifier {
    private static final float MAX_SCALE = 3.125f;
    private static final float MIN_SCALE = 0.2f;
    private final Aethelion plugin;
    
    public ScaleModifier(Aethelion plugin) {
        this.plugin = plugin;
    }
    
    public Vector3f clampScale(Vector3f scale) {
        float x = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.x()));
        float y = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.y()));
        float z = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.z()));
        
        if (!plugin.getConfig().getBoolean("aethelion.scaling.allow-non-uniform", false)) {
            float max = Math.max(x, Math.max(y, z));
            return new Vector3f(max, max, max);
        }
        return new Vector3f(x, y, z);
    }
    
    public void applyBoneScale(Entity entity, String boneName, float scaleX, float scaleY, float scaleZ) {
        Vector3f clamped = clampScale(new Vector3f(scaleX, scaleY, scaleZ));
        
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        
        // Store as string "x,y,z"
        String value = clamped.x() + "," + clamped.y() + "," + clamped.z();
        pdc.set(key, PersistentDataType.STRING, value);
    }
    
    public void applyBoneScale(Entity entity, String boneName, float scale) {
        applyBoneScale(entity, boneName, scale, scale, scale);
    }
    
    public Vector3f getBoneScale(Entity entity, String boneName) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        String value = pdc.get(key, PersistentDataType.STRING);
        
        if (value == null) {
            return new Vector3f(1.0f, 1.0f, 1.0f);
        }
        
        try {
            String[] parts = value.split(",");
            return new Vector3f(
                Float.parseFloat(parts[0]),
                Float.parseFloat(parts[1]),
                Float.parseFloat(parts[2])
            );
        } catch (Exception e) {
            return new Vector3f(1.0f, 1.0f, 1.0f);
        }
    }
    
    public float getBoneScaleUniform(Entity entity, String boneName) {
        Vector3f scale = getBoneScale(entity, boneName);
        return (scale.x() + scale.y() + scale.z()) / 3.0f;
    }
    
    public void resetBoneScale(Entity entity, String boneName) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        pdc.remove(key);
    }
    
    public void resetAllBoneScales(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        
        String[] bones = {"head", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
        for (String bone : bones) {
            NamespacedKey key = new NamespacedKey(plugin, bone + "_scale");
            pdc.remove(key);
        }
    }
}
