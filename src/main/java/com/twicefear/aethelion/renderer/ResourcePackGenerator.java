package com.twicefear.aethelion.renderer;

import com.twicefear.aethelion.Aethelion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.zip.*;

/**
 * Generates resource pack with custom textures for particles and animations
 */
public class ResourcePackGenerator {
    private final Aethelion plugin;
    private String cachedHash = null;
    
    public ResourcePackGenerator(Aethelion plugin) {
        this.plugin = plugin;
    }
    
    public void generatePack() throws IOException {
        Path outputDir = Paths.get("plugins/Aethelion/resourcepack/");
        Files.createDirectories(outputDir);
        
        Path zipPath = outputDir.resolve("pack.zip");
        
        try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            // pack.mcmeta
            addFileToZip(zos, "pack.mcmeta", generateMcMeta().getBytes());
            
            // Create texture directory for custom particles
            Path texturesDir = outputDir.resolve("assets/minecraft/textures/particle/");
            Files.createDirectories(texturesDir);
            
            // Add custom particle textures from plugin folder
            Path textureFolder = Paths.get("plugins/Aethelion/textures/");
            if (Files.exists(textureFolder)) {
                Files.walk(textureFolder)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(p -> {
                        try {
                            String name = p.getFileName().toString();
                            String path = "assets/minecraft/textures/particle/" + name;
                            addFileToZip(zos, path, Files.readAllBytes(p));
                        } catch (IOException e) {
                            plugin.getLogger().warning("Failed to add texture: " + p);
                        }
                    });
            }
            
            // Add emote textures if any
            Path emoteTexturesDir = outputDir.resolve("assets/minecraft/textures/emotes/");
            Files.createDirectories(emoteTexturesDir);
            
            Path animFolder = Paths.get(plugin.getConfig().getString("aethelion.animations.folder", "plugins/Aethelion/animations/"));
            if (Files.exists(animFolder)) {
                Files.walk(animFolder)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".png"))
                    .forEach(p -> {
                        try {
                            String name = p.getFileName().toString();
                            String path = "assets/minecraft/textures/emotes/" + name;
                            addFileToZip(zos, path, Files.readAllBytes(p));
                        } catch (IOException e) {
                            plugin.getLogger().warning("Failed to add emote texture: " + p);
                        }
                    });
            }
        }
        
        cachedHash = generateSHA1(zipPath);
        plugin.getConfig().set("aethelion.resource-pack.hash", cachedHash);
        plugin.saveConfig();
        
        plugin.getLogger().info("Resource pack generated: " + zipPath.toAbsolutePath());
    }
    
    private String generateMcMeta() {
        return String.format("""
            {
                "pack": {
                    "pack_format": %d,
                    "description": "Aethelion Resource Pack - %s"
                }
            }
            """, getPackFormat(), plugin.getDescription().getVersion());
    }
    
    private int getPackFormat() {
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch (version) {
            case "1.17": case "1.18": return 7;
            case "1.19": return 9;
            case "1.20": return 15;
            case "1.21": return 18;
            default: return 15;
        }
    }
    
    private void addFileToZip(ZipOutputStream zos, String path, byte[] data) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
    
    private String generateSHA1(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(Files.readAllBytes(file));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to generate SHA-1 hash: " + e.getMessage());
            return null;
        }
    }
    
    public String getHash() {
        if (cachedHash == null) {
            cachedHash = plugin.getConfig().getString("aethelion.resource-pack.hash");
        }
        return cachedHash;
    }
    
    public void promptPlayer(Player player) {
        if (!plugin.getConfig().getBoolean("aethelion.resource-pack.enabled")) return;
        
        String url = plugin.getConfig().getString("aethelion.resource-pack.url");
        String hash = getHash();
        boolean force = plugin.getConfig().getBoolean("aethelion.resource-pack.force", false);
        
        if (url != null && !url.isEmpty() && hash != null) {
            if (force) {
                player.setResourcePack(url, hash, true);
            } else {
                player.setResourcePack(url, hash);
            }
        }
    }
}
