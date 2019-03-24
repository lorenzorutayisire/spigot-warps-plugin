package me.loryruta.sfp.warps;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpPlugin extends JavaPlugin {
    private static WarpPlugin instance;

    public static String NO_PERMISSIONS_FOR_COMMAND;
    public static String NO_PERMISSIONS_FOR_WARP;
    public static String NO_WARP_FOR_NAME;
    public static String NO_SUB_COMMAND_FOUND;

    @Getter
    private WarpRegistry warpRegistry = new WarpRegistry();

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        NO_PERMISSIONS_FOR_COMMAND = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-permissions-for-command"));
        NO_PERMISSIONS_FOR_WARP = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-permissions-for-warp"));
        NO_WARP_FOR_NAME = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-warp-for-name"));
        NO_SUB_COMMAND_FOUND = ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-sub-command"));
    }

    @Override
    public void onEnable() {
        instance = this;

        warpRegistry.load();

        loadConfig();

        WarpCommands commands = new WarpCommands();
        getCommand("warps").setExecutor(commands);
        getCommand("warp").setExecutor(commands);
    }

    @Override
    public void onDisable() {
    }

    public static WarpPlugin get() {
        return instance;
    }
}
