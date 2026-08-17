package com.yourname.emoteengine.engine;

import com.yourname.emoteengine.EmoteEngine;
import com.yourname.emoteengine.renderer.PacketBuilder;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.joml.Vector3f;

public class ScaleModifier {
    private static final float MAX_SCALE = 3.125f;
    private static final float MIN_SCALE = 0.2f;
    private final EmoteEngine plugin;
    
    public ScaleModifier(EmoteEngine plugin) {
        this.plugin = plugin;
    }
    
    public Vector3f clampScale(Vector3f scale) {
        float x = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.x()));
        float y = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.y()));
        float z = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale.z()));
        
        if (!plugin.getConfig().getBoolean("emote-engine.scaling.allow-non-uniform", false)) {
            float max = Math.max(x, Math.max(y, z));
            return new Vector3f(max, max, max);
        }
        return new Vector3f(x, y, z);
    }
    
    public void applyBoneScale(Player player, String boneName, float scale) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        pdc.set(key, PersistentDataType.FLOAT, 
            clampScale(new Vector3f(scale, scale, scale)).x());
        
        new PacketBuilder(plugin).sendScaleUpdate(player, boneName, scale);
    }
    
    public float getBoneScale(Player player, String boneName) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        return pdc.getOrDefault(key, PersistentDataType.FLOAT, 1.0f);
    }
    
    public void resetBoneScale(Player player, String boneName) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, boneName + "_scale");
        pdc.remove(key);
        new PacketBuilder(plugin).sendScaleUpdate(player, boneName, 1.0f);
    }
}
