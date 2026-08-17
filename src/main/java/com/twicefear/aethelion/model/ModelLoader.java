package com.twicefear.aethelion.model;

import com.google.gson.*;
import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.api.BoneKeyframe;
import com.twicefear.aethelion.Aethelion;
import org.joml.Vector3f;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModelLoader {
    private final Aethelion plugin;
    private final Map<String, AnimationData> cache = new ConcurrentHashMap<>();
    private final Gson gson = new GsonBuilder().create();

    public ModelLoader(Aethelion plugin) {
        this.plugin = plugin;
    }

    public boolean registerAnimation(AnimationData animationData) {
        if (animationData == null || animationData.getId() == null) return false;
        cache.put(animationData.getId(), animationData);
        plugin.getLogger().info("Registered custom animation dynamically: " + animationData.getId());
        return true;
    }

    public boolean registerAnimation(String id, String jsonContent) {
        if (id == null || jsonContent == null || jsonContent.isEmpty()) return false;
        try {
            JsonObject obj = JsonParser.parseString(jsonContent).getAsJsonObject();
            JsonObject animData = null;

            if (obj.has("animations")) {
                JsonObject animations = obj.getAsJsonObject("animations");
                if (animations.has(id)) {
                    animData = animations.getAsJsonObject(id);
                } else if (!animations.keySet().isEmpty()) {
                    String firstKey = animations.keySet().iterator().next();
                    animData = animations.getAsJsonObject(firstKey);
                }
            } else {
                animData = obj;
            }

            if (animData == null) {
                plugin.getLogger().warning("Could not parse animation structure for registered animation: " + id);
                return false;
            }

            int length = animData.has("length") ? animData.get("length").getAsInt() : 0;
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
            cache.put(id, anim);
            plugin.getLogger().info("Successfully registered external animation: " + id);
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to register animation '" + id + "': " + e.getMessage());
            return false;
        }
    }

    public boolean registerAnimation(String id, InputStream inputStream) {
        if (id == null || inputStream == null) return false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return registerAnimation(id, builder.toString());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read input stream for animation '" + id + "': " + e.getMessage());
            return false;
        }
    }

    public AnimationData loadAnimation(String id) {
        // Check cache
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        try {
            Path folder = Paths.get(plugin.getConfig().getString("emote-engine.animations.folder", "plugins/Aethelion/animations/"));
            Path file = folder.resolve(id + ".bbmodel");

            if (!Files.exists(file)) {
                plugin.getLogger().warning("Animation not found: " + id);
                return null;
            }

            String json = Files.readString(file);
            boolean success = registerAnimation(id, json);
            return success ? cache.get(id) : null;

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load animation: " + id);
            if (plugin.getConfig().getBoolean("emote-engine.debug", false)) {
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

        // Parse scale keyframes (YOUR SPECIAL FEATURE)
        if (boneData.has("scale")) {
            JsonObject scaleData = boneData.getAsJsonObject("scale");
            parseVectorKeyframes(scaleData, keyframeMap, "scale");
        }

        return keyframeMap.values().toArray(new BoneKeyframe[0]);
    }

    private void parseVectorKeyframes(JsonObject data, Map<Integer, BoneKeyframe> map, String type) {
        if (!data.has("keyframes")) return;
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
        Set<String> anims = new TreeSet<>(cache.keySet());
        try {
            Path folder = Paths.get(plugin.getConfig().getString("emote-engine.animations.folder", "plugins/Aethelion/animations/"));
            if (Files.exists(folder)) {
                try (var stream = Files.walk(folder)) {
                    stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".bbmodel"))
                        .forEach(p -> {
                            String name = p.getFileName().toString();
                            name = name.substring(0, name.lastIndexOf('.'));
                            anims.add(name);
                        });
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to list animations: " + e.getMessage());
        }
        return new ArrayList<>(anims);
    }
}
