package com.twicefear.aethelion.renderer;

import com.twicefear.aethelion.api.AnimatedEntity;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Renders animations to entities using display entities or packets
 */
public class EntityRenderer {
    private final Aethelion plugin;
    
    public EntityRenderer(Aethelion plugin) {
        this.plugin = plugin;
    }
    
    public void sendAnimationUpdate(Entity target, AnimatedEntity ae) {
        String method = plugin.getConfig().getString("aethelion.rendering.method", "display_entities");
        
        // Check if Paper supports display entities (1.17+)
        try {
            Class.forName("org.bukkit.entity.Display");
            if (method.equals("display_entities")) {
                sendDisplayEntityUpdate(target, ae);
            } else {
                sendPacketUpdate(target, ae);
            }
        } catch (ClassNotFoundException e) {
            // Fallback to packets for older versions
            sendPacketUpdate(target, ae);
        }
    }
    
    private void sendDisplayEntityUpdate(Entity target, AnimatedEntity ae) {
        int viewDistance = plugin.getConfig().getInt("aethelion.rendering.view-distance", 64);
        for (Player p : target.getWorld().getPlayers()) {
            if (p.getLocation().distance(target.getLocation()) <= viewDistance) {
                // Send scale updates via display entity transforms
                // Implementation depends on specific rendering approach
            }
        }
    }
    
    private void sendPacketUpdate(Entity target, AnimatedEntity ae) {
        // ProtocolLib implementation for packet-based rendering
        // This would send custom packets to clients
    }
    
    public void resetEntity(Entity target) {
        int viewDistance = plugin.getConfig().getInt("aethelion.rendering.view-distance", 64);
        for (Player p : target.getWorld().getPlayers()) {
            if (p.getLocation().distance(target.getLocation()) <= viewDistance) {
                // Send reset packets to restore original model
            }
        }
    }
}
