package com.twicefear.aethelion.particle;

import com.google.gson.*;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.Color;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads particle effects from JSON files
 */
public class ParticleEffectLoader {
    private final Aethelion plugin;
    private final Map<String, ParticleEffect> loadedEffects = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().create();
    
    public ParticleEffectLoader(Aethelion plugin) {
        this.plugin = plugin;
    }
    
    public boolean loadEffect(String effectId, String jsonPath) {
        try {
            Path file = Path.of(jsonPath);
            if (!Files.exists(file)) {
                // Try relative to plugin folder
                file = Path.of("plugins/Aethelion/effects/", jsonPath);
            }
            
            if (!Files.exists(file)) {
                plugin.getLogger().warning("Particle effect file not found: " + jsonPath);
                return false;
            }
            
            String json = Files.readString(file);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            
            int duration = root.has("duration") ? root.get("duration").getAsInt() : 20;
            boolean loop = root.has("loop") && root.get("loop").getAsBoolean();
            
            List<ParticleFrame> frames = new ArrayList<>();
            
            if (root.has("frames")) {
                JsonArray framesArray = root.getAsJsonArray("frames");
                for (JsonElement elem : framesArray) {
                    JsonObject frameObj = elem.getAsJsonObject();
                    ParticleFrame frame = parseFrame(frameObj);
                    if (frame != null) {
                        frames.add(frame);
                    }
                }
            }
            
            ParticleEffect effect = new ParticleEffect(effectId, frames, duration, loop);
            loadedEffects.put(effectId, effect);
            
            plugin.getLogger().info("Loaded particle effect: " + effectId + " (" + frames.size() + " frames)");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load particle effect: " + effectId);
            if (plugin.getConfig().getBoolean("aethelion.debug", false)) {
                e.printStackTrace();
            }
            return false;
        }
    }
    
    private ParticleFrame parseFrame(JsonObject frameObj) {
        try {
            int tick = frameObj.has("tick") ? frameObj.get("tick").getAsInt() : 0;
            
            String typeStr = frameObj.has("type") ? 
                frameObj.get("type").getAsString().toUpperCase() : "FLAME";
            ParticleType type = ParticleType.valueOf(typeStr);
            
            Vector3f offset = new Vector3f(0, 0, 0);
            if (frameObj.has("offset")) {
                JsonObject offsetObj = frameObj.getAsJsonObject("offset");
                if (offsetObj.has("x")) offset.x = offsetObj.get("x").getAsFloat();
                if (offsetObj.has("y")) offset.y = offsetObj.get("y").getAsFloat();
                if (offsetObj.has("z")) offset.z = offsetObj.get("z").getAsFloat();
            }
            
            float speed = frameObj.has("speed") ? frameObj.get("speed").getAsFloat() : 0.5f;
            int count = frameObj.has("count") ? frameObj.get("count").getAsInt() : 1;
            
            ParticleData data = null;
            if (type == ParticleType.DUST && frameObj.has("color")) {
                JsonObject colorObj = frameObj.getAsJsonObject("color");
                int r = colorObj.has("r") ? colorObj.get("r").getAsInt() : 255;
                int g = colorObj.has("g") ? colorObj.get("g").getAsInt() : 255;
                int b = colorObj.has("b") ? colorObj.get("b").getAsInt() : 255;
                float size = frameObj.has("size") ? frameObj.get("size").getAsFloat() : 1.0f;
                data = new DustParticleData(Color.fromRGB(r, g, b), size);
            }
            
            return new ParticleFrame(tick, type, offset, speed, count, data);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse particle frame: " + e.getMessage());
            return null;
        }
    }
    
    public ParticleEffect getEffect(String effectId) {
        return loadedEffects.get(effectId);
    }
    
    public void removeEffect(String effectId) {
        loadedEffects.remove(effectId);
    }
    
    public void clearAll() {
        loadedEffects.clear();
    }
    
    public List<String> getLoadedEffects() {
        return new ArrayList<>(loadedEffects.keySet());
    }
}
