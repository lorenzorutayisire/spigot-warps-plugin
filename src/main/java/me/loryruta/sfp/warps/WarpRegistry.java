package me.loryruta.sfp.warps;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class WarpRegistry {
    public static File STORAGE;

    private final Map<String, Warp> warps = new HashMap<>();

    public WarpRegistry() {
    }

    public Warp register(Warp warp) {
        return warps.put(warp.getId(), warp);
    }

    public Warp getWarp(String id) {
        return warps.get(id);
    }

    public Warp unregister(String id) {
        return warps.remove(id);
    }

    public Collection<Warp> getWarps() {
        return warps.values();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        if (STORAGE == null) {
            STORAGE = new File(WarpPlugin.get().getDataFolder(), "warps.yml");
        }
        if (!STORAGE.exists()) {
            return;
        }
        FileReader reader;
        try {
            reader = new FileReader(STORAGE);
        } catch (FileNotFoundException ignored) {
            return;
        }
        Map<String, Object> warps = new Yaml().load(reader);
        for (Map<String, Object> warp : (List<Map<String, Object>>) warps.get("warps")) {
            register(Warp.load(warp));
        }
    }

    public void save() {
        FileWriter writer;
        try {
            STORAGE.getParentFile().mkdirs();
            STORAGE.createNewFile();
            writer = new FileWriter(STORAGE);
        } catch (IOException ignored) {
            return;
        }
        List<Map<String, Object>> data = new ArrayList<>();
        for (Warp warp : warps.values()) {
            data.add(warp.save());
        }
        try {
            writer.write(new Yaml().dump(new HashMap<String, Object>() {{
                put("warps", data);
            }}));
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
    }

    public void unload() {
        save();
        warps.clear();
    }
}
