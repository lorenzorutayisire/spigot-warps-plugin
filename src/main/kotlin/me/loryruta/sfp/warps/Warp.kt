package me.loryruta.sfp.warps

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

class Warp(val id: String) {
    var location: Location? = null
    var sound: Sound? = null
    var message: MutableList<String> = ArrayList()

    val permission = "warps.warp.$id"

    init {
        val parent = Bukkit.getPluginManager().getPermission("warps.warp.*")!!
        Bukkit.getPluginManager().addPermission(
                Permission(permission, PermissionDefault.TRUE).also{ it.addParent(parent, true) }
        )
    }

    fun isReady(): Boolean {
        return location != null
    }

    fun load(data: Map<String, *>) {
        if (data.containsKey("location")) {
            data["location"].also {
                location = Location(
                        Bukkit.getWorld(data["world"] as String),
                        (data["x"] as Number).toDouble(),
                        (data["y"] as Number).toDouble(),
                        (data["z"] as Number).toDouble(),
                        (data["yaw"] as Number).toFloat(),
                        (data["pitch"] as Number).toFloat()
                )
            }
        }
        if (data.containsKey("sound")) {
            sound = Sound.valueOf(data["sound"] as String)
        }
        if (data.containsKey("message")) {
            message.clear()
            message.addAll(data["message"] as List<String>)
        }
    }

    fun save(): Map<String, *> {
        val data = HashMap<String, Any>()
        data["id"] = id
        if (location != null) {
            data["location"] = HashMap<String, Any>().also {
                it["world"] = location!!.world!!.name
                it["x"] = location!!.x
                it["y"] = location!!.y
                it["z"] = location!!.z
                it["yaw"] = location!!.yaw
                it["pitch"] = location!!.pitch
            }
        }
        if (sound != null) {
            data["sound"] = sound!!.name
        }
        data["message"] = message
        return data
    }

    fun execute(player: Player): Boolean {
        if (player.hasPermission(permission)) {
            player.teleport(location!!)
            if (sound != null) {
                player.playSound(location!!, sound!!, 100.0f, 0.0f)
            }
            for (line in message) {
                player.sendMessage(line)
            }
            return true
        }
        return false
    }
}
