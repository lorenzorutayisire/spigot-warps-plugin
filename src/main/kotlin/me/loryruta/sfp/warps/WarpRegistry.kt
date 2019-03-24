package me.loryruta.sfp.warps

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader

object WarpRegistry {
    val FILE = "warps.yml"

    private val warps: MutableMap<String, Warp> = HashMap()

    fun register(warp: Warp): Warp? {
        return warps.put(warp.id, warp)
    }

    fun get(id: String): Warp? {
        return warps[id]
    }

    fun unregister(id: String): Warp? {
        return warps.remove(id)
    }

    fun list(): Collection<Warp> {
        return warps.values
    }

    fun load() {
        val reader = FileReader(File(WarpPlugin.get().dataFolder, FILE))
        val config = Yaml().load<Map<String, *>>(reader)
        for (data in config as List<Map<String, *>>) {
            val id = data["id"] as String
            warps[id] = Warp(id).also { it.load(data) }
        }
    }

    fun unload() {
        warps.clear()
    }
}
