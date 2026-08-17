package com.twicefear.aethelion.api;

import org.bukkit.entity.Player;
import java.io.InputStream;
import java.util.List;

public interface EmoteAPI {
    // Core emote methods
    boolean playEmote(Player player, String emoteId);
    void stopEmote(Player player, String emoteId);
    void stopAllEmotes(Player player);
    boolean isEmoting(Player player);

    // Scale methods (YOUR SPECIAL FEATURE)
    void setScale(Player player, String boneName, float scale);
    void resetScale(Player player, String boneName);
    float getScale(Player player, String boneName);

    // External Animation Registration (Developer Hook)
    boolean registerAnimation(String id, String jsonContent);
    boolean registerAnimation(String id, InputStream inputStream);
    boolean registerAnimation(AnimationData animationData);

    // Resource pack methods
    void promptResourcePack(Player player);
    boolean hasResourcePack(Player player);

    // Utility methods
    List<String> getAvailableEmotes();
    void reloadAnimations();
}
