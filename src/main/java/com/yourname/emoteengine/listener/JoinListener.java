package com.yourname.emoteengine.listener;

import com.yourname.emoteengine.renderer.ResourcePackGenerator;
import com.yourname.emoteengine.EmoteEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    private final ResourcePackGenerator packGenerator;
    private final EmoteEngine plugin;
    
    public JoinListener(EmoteEngine plugin) {
        this.plugin = plugin;
        this.packGenerator = new ResourcePackGenerator(plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("emote-engine.resource-pack.prompt", true)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                packGenerator.promptPlayer(event.getPlayer());
            }, 20L);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAnimationEngine().stopAllAnimations(event.getPlayer());
    }
}
