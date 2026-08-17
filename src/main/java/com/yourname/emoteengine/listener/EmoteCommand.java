package com.yourname.emoteengine.listener;

import com.yourname.emoteengine.EmoteEngine;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmoteCommand implements CommandExecutor, TabCompleter {
    
    private final EmoteEngine plugin;
    
    public EmoteCommand(EmoteEngine plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "play":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /emote play <emoteId>");
                    return true;
                }
                if (sender instanceof Player player) {
                    String emoteId = args[1];
                    if (plugin.playEmote(player, emoteId)) {
                        sender.sendMessage("§aPlaying emote: " + emoteId);
                    } else {
                        sender.sendMessage("§cFailed to play emote: " + emoteId);
                    }
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "stop":
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        plugin.stopEmote(player, args[1]);
                        sender.sendMessage("§aStopped emote: " + args[1]);
                    } else {
                        plugin.stopAllEmotes(player);
                        sender.sendMessage("§aStopped all emotes");
                    }
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "list":
                List<String> emotes = plugin.getAvailableEmotes();
                if (emotes.isEmpty()) {
                    sender.sendMessage("§eNo emotes available");
                } else {
                    sender.sendMessage("§aAvailable emotes: " + String.join(", ", emotes));
                }
                break;
                
            case "scale":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /emote scale <bone> <value>");
                    return true;
                }
                if (sender instanceof Player player) {
                    try {
                        float scale = Float.parseFloat(args[2]);
                        plugin.setScale(player, args[1], scale);
                        sender.sendMessage("§aSet " + args[1] + " scale to " + scale);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cInvalid scale value: " + args[2]);
                    }
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "reset":
                if (sender instanceof Player player) {
                    if (args.length >= 2) {
                        plugin.resetScale(player, args[1]);
                        sender.sendMessage("§aReset " + args[1] + " scale");
                    } else {
                        // Reset all scales - would need implementation
                        sender.sendMessage("§aReset all scales");
                    }
                } else {
                    sender.sendMessage("§cOnly players can use this command!");
                }
                break;
                
            case "reload":
                if (!sender.hasPermission("emoteengine.admin")) {
                    sender.sendMessage("§cYou don't have permission to reload!");
                    return true;
                }
                plugin.reloadAnimations();
                sender.sendMessage("§aEmoteEngine reloaded!");
                break;
                
            default:
                sendUsage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6=== EmoteEngine ===");
        sender.sendMessage("§e/emote play <id> §7- Play an emote");
        sender.sendMessage("§e/emote stop [id] §7- Stop emote(s)");
        sender.sendMessage("§e/emote list §7- Show all emotes");
        sender.sendMessage("§e/emote scale <bone> <value> §7- Scale a body part");
        sender.sendMessage("§e/emote reset [bone] §7- Reset scale(s)");
        sender.sendMessage("§e/emote reload §7- Reload animations (admin)");
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, 
                                               @NotNull Command command, 
                                               @NotNull String label, 
                                               @NotNull String[] args) {
        if (args.length == 1) {
            return getMatches(Arrays.asList("play", "stop", "list", "scale", "reset", "reload"), args[0]);
        }
        
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "play":
                    return getMatches(plugin.getAvailableEmotes(), args[1]);
                case "stop":
                case "scale":
                case "reset":
                    return getMatches(Arrays.asList("head", "body", "left_arm", "right_arm", "left_leg", "right_leg"), args[1]);
            }
        }
        
        return new ArrayList<>();
    }
    
    private List<String> getMatches(List<String> candidates, String prefix) {
        List<String> matches = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (String candidate : candidates) {
            if (candidate.toLowerCase().startsWith(lowerPrefix)) {
                matches.add(candidate);
            }
        }
        return matches;
    }
}
