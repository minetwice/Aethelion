package com.twicefear.aethelion.loader;

import com.google.gson.*;
import com.twicefear.aethelion.Aethelion;
import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.api.BoneKeyframe;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads Blockbench animations from .bbmodel files
 */
public class AnimationLoader {
    private final Aethelion plugin;
    private final Map<String, AnimationData> cache = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().create();
    
    public AnimationLoader(Aethelion plugin) {
        this.plugin = plugin;
    }
    
    public AnimationData loadAnimation(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        
        try {
            Path folder = Path.of(plugin.getConfig().getString("aethelion.animations.folder", "plugins/Aethelion/animations/"));
            Path file = folder.resolve(id + ".bbmodel");
            
            if (!Files.exists(file)) {
                plugin.getLogger().warning("Animation not found: " + id);
                return null;
            }
            
            String json = Files.readString(file);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            
            // Parse animations
            JsonObject animations = obj.has("animations") ? 
                obj.getAsJsonObject("animations") : null;
            
            if (animations == null || !animations.has(id)) {
                plugin.getLogger().warning("Animation '" + id + "' not found in file");
                return null;
            }
            
            JsonObject animData = animations.getAsJsonObject(id);
            int length = animData.has("length") ? animData.get("length").getAsInt() : 20;
            boolean loop = animData.has("loop") && animData.get("loop").getAsBoolean();
            
            Map<String, BoneKeyframe[]> boneAnimations = new HashMap<>();
            
            if (animData.has("bones")) {
                JsonObject bones = animData.getAsJsonObject("bones");
                for (String boneName : bones.keySet()) {
                    JsonObject boneData = bones.getAsJsonObject(boneName);
                    BoneKeyframe[] keyframes = parseBoneKeyframes(boneData);
                    boneAnimations.put(boneName, keyframes);
                }
            }
            
            AnimationData anim = new AnimationData(id, boneAnimations, length, loop);
            
            if (plugin.getConfig().getBoolean("aethelion.animations.cache", true)) {
                cache.put(id, anim);
            }
            
            return anim;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load animation: " + id);
            if (plugin.getConfig().getBoolean("aethelion.debug", false)) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    private BoneKeyframe[] parseBoneKeyframes(JsonObject boneData) {
        Map<Integer, BoneKeyframe> keyframeMap = new TreeMap<>();
        
        // Parse position keyframes
        if (boneData.has("position")) {
            JsonObject posData = boneData.getAsJsonObject("position");
            parseVectorKeyframes(posData, keyframeMap, "position");
        }
        
        // Parse rotation keyframes
        if (boneData.has("rotation")) {
            JsonObject rotData = boneData.getAsJsonObject("rotation");
            parseVectorKeyframes(rotData, keyframeMap, "rotation");
        }
        
        // Parse scale keyframes
        if (boneData.has("scale")) {
            JsonObject scaleData = boneData.getAsJsonObject("scale");
            parseVectorKeyframes(scaleData, keyframeMap, "scale");
        }
        
        return keyframeMap.values().toArray(new BoneKeyframe[0]);
    }
    
    private void parseVectorKeyframes(JsonObject data, Map<Integer, BoneKeyframe> map, String type) {
        JsonArray keyframes = data.getAsJsonArray("keyframes");
        for (JsonElement elem : keyframes) {
            JsonObject frame = elem.getAsJsonObject();
            int tick = frame.has("t") ? frame.get("t").getAsInt() : 0;
            
            Vector3f vector = new Vector3f(0, 0, 0);
            if (frame.has("x")) vector.x = frame.get("x").getAsFloat();
            if (frame.has("y")) vector.y = frame.get("y").getAsFloat();
            if (frame.has("z")) vector.z = frame.get("z").getAsFloat();
            
            BoneKeyframe existing = map.get(tick);
            if (existing == null) {
                Vector3f pos = type.equals("position") ? vector : new Vector3f(0, 0, 0);
                Vector3f rot = type.equals("rotation") ? vector : new Vector3f(0, 0, 0);
                Vector3f scale = type.equals("scale") ? vector : new Vector3f(1, 1, 1);
                map.put(tick, new BoneKeyframe(tick, pos, rot, scale));
            } else {
                Vector3f pos = type.equals("position") ? vector : existing.getPosition();
                Vector3f rot = type.equals("rotation") ? vector : existing.getRotation();
                Vector3f scale = type.equals("scale") ? vector : existing.getScale();
                map.put(tick, new BoneKeyframe(tick, pos, rot, scale));
            }
        }
    }
    
    public void reloadCache() {
        cache.clear();
        plugin.getLogger().info("Animation cache reloaded!");
    }
    
    public List<String> getAvailableAnimations() {
        List<String> anims = new ArrayList<>();
        try {
            Path folder = Path.of(plugin.getConfig().getString("aethelion.animations.folder", "plugins/Aethelion/animations/"));
            if (Files.exists(folder)) {
                Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".bbmodel"))
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        name = name.substring(0, name.lastIndexOf('.'));
                        anims.add(name);
                    });
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to list animations: " + e.getMessage());
        }
        return anims;
    }
}
