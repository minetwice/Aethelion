package com.twicefear.aethelion.listener;

import com.twicefear.aethelion.Aethelion;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command handler for Aethelion library
 * Note: This is mainly for testing/debugging. Library is designed for API use.
 */
public class AethelionCommand implements CommandExecutor, TabCompleter {
    private final Aethelion plugin;
    
    public AethelionCommand(Aethelion plugin) {
        this.plugin = plugin;
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
                p.sendMessage(ChatColor.GREEN + "Available animations: " + 
                    String.join(", ", plugin.getAPI().getAvailableAnimations()));
                break;
                
            case "play":
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage: /aethelion play <animationId>");
                    return true;
                }
                if (plugin.getAPI().playAnimation(p, args[1])) {
                    p.sendMessage(ChatColor.GREEN + "Playing animation: " + args[1]);
                } else {
                    p.sendMessage(ChatColor.RED + "Animation not found: " + args[1]);
                }
                break;
                
            case "stop":
                plugin.getAPI().stopAllAnimations(p);
                p.sendMessage(ChatColor.GREEN + "Stopped all animations");
                break;
                
            case "scale":
                if (args.length < 3) {
                    p.sendMessage(ChatColor.RED + "Usage: /aethelion scale <boneName> <scale>");
                    p.sendMessage(ChatColor.YELLOW + "Bones: head, body, left_arm, right_arm, left_leg, right_leg");
                    return true;
                }
                try {
                    float scale = Float.parseFloat(args[2]);
                    plugin.getAPI().setBoneScale(p, args[1], scale);
                    p.sendMessage(ChatColor.GREEN + "Set " + args[1] + " scale to " + scale);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Invalid scale value");
                }
                break;
                
            case "reset":
                if (args.length > 1) {
                    plugin.getAPI().resetBoneScale(p, args[1]);
                    p.sendMessage(ChatColor.GREEN + "Reset scale for " + args[1]);
                } else {
                    plugin.getAPI().resetAllBoneScales(p);
                    p.sendMessage(ChatColor.GREEN + "Reset all scales");
                }
                break;
                
            case "effects":
                p.sendMessage(ChatColor.GREEN + "Loaded effects: " + 
                    String.join(", ", plugin.getAPI().getLoadedEffects()));
                break;
                
            case "reload":
                if (!p.hasPermission("aethelion.admin")) {
                    p.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                plugin.getAPI().reloadAll();
                p.sendMessage(ChatColor.GREEN + "Reloaded animations and effects");
                break;
                
            default:
                sendHelp(p);
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "play", "stop", "scale", "reset", "effects", "reload");
        }
        if (args.length == 2 && args[0].equals("play")) {
            return new ArrayList<>(plugin.getAPI().getAvailableAnimations());
        }
        if (args.length == 2 && args[0].equals("scale")) {
            return Arrays.asList("head", "body", "left_arm", "right_arm", "left_leg", "right_leg");
        }
        if (args.length == 2 && args[0].equals("reset")) {
            return Arrays.asList("head", "body", "left_arm", "right_arm", "left_leg", "right_leg");
        }
        return new ArrayList<>();
    }
    
    private void sendHelp(Player p) {
        p.sendMessage(ChatColor.GOLD + "=== Aethelion Library Commands ===");
        p.sendMessage(ChatColor.YELLOW + "/aethelion list " + ChatColor.WHITE + "- Show available animations");
        p.sendMessage(ChatColor.YELLOW + "/aethelion play <id> " + ChatColor.WHITE + "- Play an animation");
        p.sendMessage(ChatColor.YELLOW + "/aethelion stop " + ChatColor.WHITE + "- Stop all animations");
        p.sendMessage(ChatColor.YELLOW + "/aethelion scale <bone> <value> " + ChatColor.WHITE + "- Scale a bone");
        p.sendMessage(ChatColor.YELLOW + "/aethelion reset [bone] " + ChatColor.WHITE + "- Reset bone scale");
        p.sendMessage(ChatColor.YELLOW + "/aethelion effects " + ChatColor.WHITE + "- Show loaded particle effects");
        p.sendMessage(ChatColor.YELLOW + "/aethelion reload " + ChatColor.WHITE + "- Reload (admin only)");
        p.sendMessage(ChatColor.GRAY + "This is a library plugin for developers.");
    }
}
