package com.twicefear.aethelion.particle;

import com.google.gson.*;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleEngine {
    private final Aethelion plugin;
    private final Map<String, CustomParticleData> particleRegistry = new ConcurrentHashMap<>();

    public ParticleEngine(Aethelion plugin) {
        this.plugin = plugin;
    }

    public boolean registerParticle(String id, String jsonContent) {
        if (id == null || jsonContent == null || jsonContent.isEmpty()) return false;
        try {
            JsonObject obj = JsonParser.parseString(jsonContent).getAsJsonObject();
            if (obj.has("particle")) {
                obj = obj.getAsJsonObject("particle");
            }

            String typeStr = obj.has("type") ? obj.get("type").getAsString().toUpperCase() : "REDSTONE";
            Particle particleType;
            try {
                particleType = Particle.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                // Fallback to REDSTONE / DUST if type not directly found
                particleType = Particle.REDSTONE;
            }

            Color color = null;
            if (obj.has("color")) {
                JsonObject colObj = obj.getAsJsonObject("color");
                int r = colObj.has("r") ? colObj.get("r").getAsInt() : 255;
                int g = colObj.has("g") ? colObj.get("g").getAsInt() : 255;
                int b = colObj.has("b") ? colObj.get("b").getAsInt() : 255;
                color = Color.fromRGB(r, g, b);
            }

            float size = obj.has("size") ? obj.get("size").getAsFloat() : 1.0f;

            Material material = null;
            if (obj.has("material")) {
                String matStr = obj.get("material").getAsString().toUpperCase();
                material = Material.matchMaterial(matStr);
            }

            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            double offsetX = obj.has("offsetX") ? obj.get("offsetX").getAsDouble() : 0.0;
            double offsetY = obj.has("offsetY") ? obj.get("offsetY").getAsDouble() : 0.0;
            double offsetZ = obj.has("offsetZ") ? obj.get("offsetZ").getAsDouble() : 0.0;
            double speed = obj.has("speed") ? obj.get("speed").getAsDouble() : 0.01;

            CustomParticleData particleData = new CustomParticleData(
                id, particleType, color, size, material, count, offsetX, offsetY, offsetZ, speed
            );

            particleRegistry.put(id, particleData);
            plugin.getLogger().info("Registered custom particle design without resource pack: " + id);
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to parse custom particle '" + id + "': " + e.getMessage());
            return false;
        }
    }

    public boolean registerParticle(String id, InputStream inputStream) {
        if (id == null || inputStream == null) return false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return registerParticle(id, builder.toString());
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read particle input stream for '" + id + "': " + e.getMessage());
            return false;
        }
    }

    public boolean spawnParticle(Location location, String particleId) {
        CustomParticleData data = particleRegistry.get(particleId);
        if (data == null) {
            plugin.getLogger().warning("Custom particle id not found: " + particleId);
            return false;
        }
        return spawnParticle(location, data);
    }

    public boolean spawnParticle(Location location, CustomParticleData data) {
        if (location == null || location.getWorld() == null || data == null) return false;

        World world = location.getWorld();
        Particle particle = data.getType();

        if (particle == Particle.REDSTONE && data.getColor() != null) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(data.getColor(), data.getSize());
            world.spawnParticle(particle, location, data.getCount(), data.getOffsetX(), data.getOffsetY(), data.getOffsetZ(), data.getSpeed(), dustOptions);
            return true;
        }

        if ((particle == Particle.ITEM_CRACK || particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_DUST) && data.getMaterial() != null) {
            if (particle == Particle.ITEM_CRACK) {
                ItemStack item = new ItemStack(data.getMaterial());
                world.spawnParticle(particle, location, data.getCount(), data.getOffsetX(), data.getOffsetY(), data.getOffsetZ(), data.getSpeed(), item);
            } else {
                world.spawnParticle(particle, location, data.getCount(), data.getOffsetX(), data.getOffsetY(), data.getOffsetZ(), data.getSpeed(), data.getMaterial().createBlockData());
            }
            return true;
        }

        world.spawnParticle(particle, location, data.getCount(), data.getOffsetX(), data.getOffsetY(), data.getOffsetZ(), data.getSpeed());
        return true;
    }

    public boolean spawnParticleOnPlayer(Player player, String particleId) {
        if (player == null) return false;
        return spawnParticle(player.getLocation().add(0, 1.0, 0), particleId);
    }

    public List<String> getRegisteredParticleIds() {
        return new ArrayList<>(particleRegistry.keySet());
    }
}
