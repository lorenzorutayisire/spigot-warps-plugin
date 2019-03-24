package me.loryruta.sfp.warps;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;

public class Warp {
    @Getter
    private final String id;

    @Getter
    private final Location location;

    @Getter
    private final Permission permission;

    @Getter
    @Setter
    private Sound sound;

    @Getter
    @Setter
    private List<String> message = new ArrayList<>();

    public Warp(String id, Location location) {
        this.id = id;
        this.location = location;
        this.permission = new Permission("warps.warp." + id);
        this.permission.addParent(Bukkit.getPluginManager().getPermission("warps.warp.*"), true);
    }

    public boolean execute(Player player) {
        if (player.hasPermission(permission)) {
            player.teleport(location);
            if (sound != null) {
                player.playSound(location, sound, 100.0f, 0.0f);
            }
            for (String line : message) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
            return true;
        }
        return false;
    }

    public Map<String, Object> save() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id);

        Map<String, Object> location = new HashMap<>();
        location.put("world", this.location.getWorld());
        location.put("x", this.location.getX());
        location.put("y", this.location.getY());
        location.put("z", this.location.getZ());
        location.put("yaw", this.location.getYaw());
        location.put("pitch", this.location.getPitch());

        data.put("location", location);
        if (sound != null) {
            data.put("sound", sound.name());
        }
        data.put("message", message);

        return data;
    }

    @SuppressWarnings("unchecked")
    public static Warp load(Map<String, Object> data) {
        Map<String, Object> location = (Map<String, Object>) data.get("location");
        Warp warp = new Warp((String) data.get("id"), new Location(
                Bukkit.getWorld((String) data.get("world")),
                ((Number) location.get("x")).doubleValue(),
                ((Number) location.get("y")).doubleValue(),
                ((Number) location.get("z")).doubleValue(),
                ((Number) location.get("yaw")).floatValue(),
                ((Number) location.get("pitch")).floatValue()
        ));

        if (data.containsKey("sound")) {
            warp.sound = Sound.valueOf((String) data.get("sound"));
        }

        Object message = data.get("message");
        if (message instanceof String) {
            warp.message = Collections.singletonList((String) message);
        } else if (message instanceof List) {
            warp.message = (List<String>) message;
        }

        return warp;
    }
}
