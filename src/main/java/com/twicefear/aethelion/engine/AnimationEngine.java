package com.twicefear.aethelion.engine;

import com.twicefear.aethelion.api.EmotePlayer;
import com.twicefear.aethelion.api.AnimationData;
import com.twicefear.aethelion.api.AnimationState;
import com.twicefear.aethelion.model.ModelLoader;
import com.twicefear.aethelion.renderer.PacketBuilder;
import com.twicefear.aethelion.Aethelion;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationEngine {
    private final Map<UUID, EmotePlayer> activePlayers = new ConcurrentHashMap<>();
    private final ModelLoader modelLoader;
    private final PacketBuilder packetBuilder;
    private final KeyframeInterpolator interpolator;
    private final Aethelion plugin;

    public AnimationEngine(Aethelion plugin) {
        this.plugin = plugin;
        this.modelLoader = plugin.getModelLoader();
        this.packetBuilder = new PacketBuilder(plugin);
        this.interpolator = new KeyframeInterpolator();
    }

    public boolean playAnimation(Player player, String animationId) {
        AnimationData anim = modelLoader.loadAnimation(animationId);
        if (anim == null) return false;

        EmotePlayer ep = activePlayers.computeIfAbsent(player.getUniqueId(),
            uuid -> new EmotePlayer(player));

        AnimationState state = new AnimationState(anim);
        ep.addAnimation(state);

        packetBuilder.sendAnimationUpdate(player, ep);
        return true;
    }

    public void stopAnimation(Player player, String animationId) {
        EmotePlayer ep = activePlayers.get(player.getUniqueId());
        if (ep != null) {
            ep.removeAnimation(animationId);
        }
    }

    public void stopAllAnimations(Player player) {
        EmotePlayer ep = activePlayers.get(player.getUniqueId());
        if (ep != null) {
            ep.clearAnimations();
            packetBuilder.resetPlayer(player);
        }
    }

    public void tick() {
        for (EmotePlayer ep : activePlayers.values()) {
            if (ep.getActiveAnimations().isEmpty()) continue;

            ep.update();
            Player player = plugin.getServer().getPlayer(ep.getPlayerId());
            if (player != null && player.isOnline()) {
                packetBuilder.sendAnimationUpdate(player, ep);
            }
        }
    }

    public boolean isEmoting(Player player) {
        EmotePlayer ep = activePlayers.get(player.getUniqueId());
        return ep != null && !ep.getActiveAnimations().isEmpty();
    }

    public void cleanup() {
        activePlayers.clear();
    }
}
