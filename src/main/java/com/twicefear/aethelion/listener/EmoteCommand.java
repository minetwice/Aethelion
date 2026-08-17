package com.twicefear.aethelion.listener;

import com.twicefear.aethelion.api.EmoteAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmoteCommand implements CommandExecutor {
    private final EmoteAPI api;

    public EmoteCommand(EmoteAPI api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        String subCmd = args[0].toLowerCase();

        switch (subCmd) {
            case "list":
                p.sendMessage(ChatColor.GREEN + "Emotes: " +
                    String.join(", ", api.getAvailableEmotes()));
                break;

            case "play":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /emote play <emoteId>");
                    return true;
                }
                if (api.playEmote(p, args[1])) {
                    p.sendMessage(ChatColor.GREEN + "Playing emote: " + args[1]);
                } else {
                    p.sendMessage(ChatColor.RED + "Emote not found: " + args[1]);
                }
                break;

            case "stop":
                api.stopAllEmotes(p);
                p.sendMessage(ChatColor.GREEN + "Stopped all emotes");
                break;

            case "scale":
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Usage: /emote scale <boneName> <scale>");
                    p.sendMessage(ChatColor.YELLOW + "Bones: head, body, left_arm, right_arm, left_leg, right_leg");
                    return true;
                }
                try {
                    float scale = Float.parseFloat(args[2]);
                    api.setScale(p, args[1], scale);
                    p.sendMessage(ChatColor.GREEN + "Set " + args[1] + " scale to " + scale);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Invalid scale value");
                }
                break;

            case "reset":
                if (args.length > 1) {
                    api.resetScale(p, args[1]);
                    p.sendMessage(ChatColor.GREEN + "Reset scale for " + args[1]);
                } else {
                    api.resetScale(p, null);
                    p.sendMessage(ChatColor.GREEN + "Reset all scales");
                }
                break;

            case "reload":
                if (!p.hasPermission("emoteengine.admin")) {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                api.reloadAnimations();
                p.sendMessage(ChatColor.GREEN + "Reloaded animations");
                break;

            default:
                sendHelp(p);
        }
        return true;
    }

    private void sendHelp(Player p) {
        p.sendMessage(ChatColor.GOLD + "=== Aethelion Commands ===");
        p.sendMessage(ChatColor.YELLOW + "/emote list " + ChatColor.WHITE + "- Show all emotes");
        p.sendMessage(ChatColor.YELLOW + "/emote play <id> " + ChatColor.WHITE + "- Play an emote");
        p.sendMessage(ChatColor.YELLOW + "/emote stop " + ChatColor.WHITE + "- Stop all emotes");
        p.sendMessage(ChatColor.YELLOW + "/emote scale <bone> <value> " + ChatColor.WHITE + "- Scale a body part");
        p.sendMessage(ChatColor.YELLOW + "/emote reset [bone] " + ChatColor.WHITE + "- Reset scale");
        p.sendMessage(ChatColor.YELLOW + "/emote reload " + ChatColor.WHITE + "- Reload animations (admin)");
        p.sendMessage(ChatColor.GRAY + "Bones: head, body, left_arm, right_arm, left_leg, right_leg");
    }
}
