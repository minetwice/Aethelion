package com.twicefear.aethelion.listener;

import com.twicefear.aethelion.Aethelion;
import com.twicefear.aethelion.renderer.ResourcePackGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    private final ResourcePackGenerator packGenerator;
    private final Aethelion plugin;

    public JoinListener(Aethelion plugin) {
        this.plugin = plugin;
        this.packGenerator = plugin.getPackGenerator();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("emote-engine.resource-pack.prompt", true)) {
            // Delay to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                packGenerator.promptPlayer(event.getPlayer());
            }, 20L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up animation data
        plugin.getAnimationEngine().stopAllAnimations(event.getPlayer());
    }
}
