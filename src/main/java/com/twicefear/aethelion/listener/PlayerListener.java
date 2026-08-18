package com.twicefear.aethelion.listener;

import com.twicefear.aethelion.renderer.ResourcePackGenerator;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join/quit events for resource pack and cleanup
 */
public class PlayerListener implements Listener {
    private final ResourcePackGenerator packGenerator;
    private final Aethelion plugin;
    
    public PlayerListener(Aethelion plugin) {
        this.plugin = plugin;
        this.packGenerator = new ResourcePackGenerator(plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("aethelion.resource-pack.prompt", true)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                packGenerator.promptPlayer(event.getPlayer());
            }, 20L);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAnimationEngine().stopAllAnimations(event.getPlayer());
        plugin.getParticlePlayer().stopAllOnEntity(event.getPlayer());
    }
}
