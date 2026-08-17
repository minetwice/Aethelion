package com.yourname.emoteengine.compat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

public class VersionAdapter {
    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final String SERVER_VERSION;
    
    static {
        SERVER_VERSION = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = SERVER_VERSION.split("\\.");
        MAJOR_VERSION = Integer.parseInt(parts[0]);
        MINOR_VERSION = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
    }
    
    public static boolean isPaper() {
        try {
            Class.forName("io.papermc.paper.api.PaperAPI");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public static boolean supportsDisplayEntities() {
        return MAJOR_VERSION >= 1 && MINOR_VERSION >= 17;
    }
    
    public static boolean supportsPersistentData() {
        return MAJOR_VERSION >= 1 && MINOR_VERSION >= 14;
    }
    
    public static boolean supports1_21() {
        return MAJOR_VERSION >= 1 && MINOR_VERSION >= 21;
    }
    
    public static String getServerVersion() {
        return SERVER_VERSION;
    }
    
    public static int getMajorVersion() {
        return MAJOR_VERSION;
    }
    
    public static int getMinorVersion() {
        return MINOR_VERSION;
    }
}
