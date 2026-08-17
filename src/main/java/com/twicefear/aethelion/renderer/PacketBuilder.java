package com.twicefear.aethelion.renderer;

import com.twicefear.aethelion.Aethelion;
import com.twicefear.aethelion.api.EmotePlayer;
import com.twicefear.aethelion.engine.ScaleModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PacketBuilder {
    private final Aethelion plugin;
    private final ScaleModifier scaleModifier;

    public PacketBuilder(Aethelion plugin) {
        this.plugin = plugin;
        this.scaleModifier = plugin.getScaleModifier();
    }

    public void sendAnimationUpdate(Player target, EmotePlayer ep) {
        // Use display entities or packets
        String method = plugin.getConfig().getString("emote-engine.rendering.method", "display_entities");

        // Check if Paper supports display entities (1.17+)
        try {
            Class.forName("org.bukkit.entity.Display");
            if ("display_entities".equals(method)) {
                sendDisplayEntityUpdate(target, ep);
            } else {
                sendPacketUpdate(target, ep);
            }
        } catch (ClassNotFoundException e) {
            // Fallback to packets for older versions
            sendPacketUpdate(target, ep);
        }
    }

    private void sendDisplayEntityUpdate(Player target, EmotePlayer ep) {
        // For each bone, update display entity
        // This is a simplified version - in production use actual display entities
        Location loc = target.getLocation().clone();
        loc.add(0, 1.5, 0);

        // Broadcast to all players in view distance
        int viewDistance = plugin.getConfig().getInt("emote-engine.rendering.view-distance", 64);
        for (Player p : target.getWorld().getPlayers()) {
            if (p.getLocation().distance(target.getLocation()) <= viewDistance) {
                // Send custom display entity update
                sendScaleUpdate(target, "body", getScale(target, "body"));
            }
        }
    }

    private void sendPacketUpdate(Player target, EmotePlayer ep) {
        // Raw packet approach - send entity metadata
        // Using ProtocolLib if available
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            sendProtocolLibPacket(target, ep);
        }
    }

    private void sendProtocolLibPacket(Player target, EmotePlayer ep) {
        // ProtocolLib implementation placeholder
        // In production, implement proper packet sending
    }

    public void sendScaleUpdate(Player target, String boneName, float scale) {
        // Broadcast scale change to all players
        int viewDistance = plugin.getConfig().getInt("emote-engine.rendering.view-distance", 64);
        for (Player p : target.getWorld().getPlayers()) {
            if (p.getLocation().distance(target.getLocation()) <= viewDistance) {
                // Send scale update packet
                // Use ProtocolLib or raw NMS
            }
        }
    }

    public void resetPlayer(Player target) {
        // Reset all transforms
        int viewDistance = plugin.getConfig().getInt("emote-engine.rendering.view-distance", 64);
        for (Player p : target.getWorld().getPlayers()) {
            if (p.getLocation().distance(target.getLocation()) <= viewDistance) {
                // Send reset packets
            }
        }
    }

    private float getScale(Player player, String boneName) {
        return scaleModifier != null ? scaleModifier.getBoneScale(player, boneName) : 1.0f;
    }
}
