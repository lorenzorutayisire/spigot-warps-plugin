package me.loryruta.sfp.warps;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

import static me.loryruta.sfp.warps.WarpPlugin.NO_PERMISSIONS_FOR_COMMAND;
import static me.loryruta.sfp.warps.WarpPlugin.NO_PERMISSIONS_FOR_WARP;
import static me.loryruta.sfp.warps.WarpPlugin.NO_WARP_FOR_NAME;
import static org.bukkit.ChatColor.*;

public class WarpCommands implements CommandExecutor {
    // /warps create <warp>
    private void onWarpCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.create")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (args.length != 1) {
            sender.sendMessage(RED + "Invalid syntax, expected: /warps create <name>");
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(RED + "Only players can run this command.");
            return;
        }
        String id = args[0];
        Warp old = WarpPlugin.get().getWarpRegistry().register(new Warp(id, ((Player) sender).getLocation()));
        sender.sendMessage(GREEN + "Warp " + YELLOW + id + GREEN + (old == null ? " created." : " replaced."));
    }

    // /warps setsound <warp> <sound>
    private void onWarpSetSound(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.setsound")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(RED + "Invalid syntax, expected: /warps setsound <warp> <sound>");
            return;
        }
        Warp warp = WarpPlugin.get().getWarpRegistry().getWarp(args[0]);
        if (warp == null) {
            sender.sendMessage(RED + "No warp for: " + args[0] + ".");
            return;
        }
        Sound sound;
        try {
            sound = Sound.valueOf(args[1]);
        } catch (IllegalArgumentException ignored) {
            sender.sendMessage(RED + "Invalid sound: " + args[1] + ".");
            return;
        }
        sender.sendMessage(GREEN + "Sound " + YELLOW + sound.name() + GREEN + " attached to " + YELLOW + warp.getId() + GREEN + ".");
    }

    // /warps setmessage <warp> <message>
    private void onWarpSetMessage(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.message")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(RED + "Invalid syntax, expected: /warps setmessage <warp> <message>");
            return;
        }
        Warp warp = WarpPlugin.get().getWarpRegistry().getWarp(args[0]);
        if (warp == null) {
            sender.sendMessage(RED + "No warp for: " + args[0] + ".");
            return;
        }
        warp.getMessage().add(args[1]);
        sender.sendMessage(GREEN +
                "Message attached to the warp: " + YELLOW + warp.getId() + GREEN + ". " +
                "If you'd like to add a longer message, consider editing it in configuration file.");
    }

    // /warps delete <warp>
    private void onWarpDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.delete")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (args.length != 2) {
            sender.sendMessage(RED + "Wrong syntax. Expected: /warps delete <warp>.");
            return;
        }
        String id = args[1];
        if (WarpPlugin.get().getWarpRegistry().unregister(id) == null) {
            sender.sendMessage(RED + "Warp not found for: " + id + ".");
            return;
        }
        sender.sendMessage(GREEN + "Warp " + YELLOW + id + GREEN + " deleted.");
    }

    // /warps
    private void onWarps(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.list")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (args.length != 0) {
            sender.sendMessage(RED + "Wrong syntax. Expected: /warps");
            return;
        }
        Collection<Warp> warps = WarpPlugin.get().getWarpRegistry().getWarps();
        sender.sendMessage(GREEN + "There are " + YELLOW + warps.size() + "" + GREEN + ":");
        for (Warp warp : warps) {
            sender.sendMessage(GREEN + "- " + YELLOW + warp.getId());
        }
    }

    // /warp
    private void onWarp(CommandSender sender, String[] args) {
        if (!sender.hasPermission("warps.warp")) {
            sender.sendMessage(NO_PERMISSIONS_FOR_COMMAND);
            return;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage(RED + "Only players can run this command.");
            return;
        }
        Warp warp = WarpPlugin.get().getWarpRegistry().getWarp(args[0]);
        if (warp == null) {
            sender.sendMessage(NO_WARP_FOR_NAME);
            return;
        }
        if (!warp.execute((Player) sender)) {
            sender.sendMessage(NO_PERMISSIONS_FOR_WARP);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label) {
            case "warps":
                if (args.length > 0) {
                    String[] subArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                    switch (args[0]) {
                        case "create":
                        case "add":
                            onWarpCreate(sender, args);
                            break;
                        case "delete":
                        case "remove":
                            onWarpDelete(sender, args);
                            break;
                        case "setsound":
                            onWarpSetSound(sender, args);
                            break;
                        case "setmessage":
                            onWarpSetMessage(sender, args);
                            break;
                        default:
                            sender.sendMessage(RED + "No sub-command found for: " + args[0] + ". Try: /warps help"); // TODO config
                            break;
                    }
                } else {
                    onWarps(sender, args);
                }
            case "warp":
                onWarp(sender, args);
                break;
            default:
                return false;
        }
        return true;
    }
}
