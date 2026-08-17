package com.twicefear.aethelion.model;

import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.api.BoneKeyframe;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UniversalModelLoader {

    public enum ModelFormat {
        OBJ,
        JSON,
        BBMODEL,
        UNKNOWN
    }

    public static class UniversalModelData {
        private final String id;
        private final ModelFormat format;
        private final Map<String, List<Vector3f>> verticesByGroup;
        private final Map<String, List<int[]>> facesByGroup;
        private final String textureData;
        private final AnimationData animationData;

        public UniversalModelData(String id, ModelFormat format, Map<String, List<Vector3f>> verticesByGroup, Map<String, List<int[]>> facesByGroup, String textureData, AnimationData animationData) {
            this.id = id;
            this.format = format;
            this.verticesByGroup = verticesByGroup;
            this.facesByGroup = facesByGroup;
            this.textureData = textureData;
            this.animationData = animationData;
        }

        public String getId() { return id; }
        public ModelFormat getFormat() { return format; }
        public Map<String, List<Vector3f>> getVerticesByGroup() { return verticesByGroup; }
        public Map<String, List<int[]>> getFacesByGroup() { return facesByGroup; }
        public String getTextureData() { return textureData; }
        public AnimationData getAnimationData() { return animationData; }
    }

    public static UniversalModelData parseObjModel(String id, InputStream objStream, InputStream mtlStream) {
        Map<String, List<Vector3f>> verticesByGroup = new HashMap<>();
        Map<String, List<int[]>> facesByGroup = new HashMap<>();
        List<Vector3f> allVertices = new ArrayList<>();
        String currentGroup = "default";

        verticesByGroup.put(currentGroup, new ArrayList<>());
        facesByGroup.put(currentGroup, new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) continue;

                if (line.startsWith("o ") || line.startsWith("g ")) {
                    currentGroup = line.substring(2).trim();
                    verticesByGroup.putIfAbsent(currentGroup, new ArrayList<>());
                    facesByGroup.putIfAbsent(currentGroup, new ArrayList<>());
                } else if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    Vector3f v = new Vector3f(x, y, z);
                    allVertices.add(v);
                    verticesByGroup.get(currentGroup).add(v);
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    int[] faceIndices = new int[parts.length - 1];
                    for (int i = 1; i < parts.length; i++) {
                        String vertexData = parts[i].split("/")[0];
                        faceIndices[i - 1] = Integer.parseInt(vertexData) - 1;
                    }
                    facesByGroup.get(currentGroup).add(faceIndices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, BoneKeyframe[]> defaultKeyframes = new HashMap<>();
        defaultKeyframes.put("root", new BoneKeyframe[]{
            new BoneKeyframe(0, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1))
        });
        AnimationData dummyAnim = new AnimationData(id, defaultKeyframes, 20, true);

        return new UniversalModelData(id, ModelFormat.OBJ, verticesByGroup, facesByGroup, null, dummyAnim);
    }

    public static ModelFormat detectFormat(String content) {
        if (content == null) return ModelFormat.UNKNOWN;
        String trimmed = content.trim();
        if (trimmed.startsWith("v ") || trimmed.startsWith("o ") || trimmed.startsWith("g ") || trimmed.startsWith("#")) {
            return ModelFormat.OBJ;
        }
        if (trimmed.startsWith("{") && trimmed.contains("\"animations\"")) {
            return ModelFormat.BBMODEL;
        }
        if (trimmed.startsWith("{")) {
            return ModelFormat.JSON;
        }
        return ModelFormat.UNKNOWN;
    }
}
