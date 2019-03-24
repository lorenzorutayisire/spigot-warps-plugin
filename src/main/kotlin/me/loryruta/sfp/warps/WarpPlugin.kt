package me.loryruta.sfp.warps

import org.bukkit.ChatColor
import org.bukkit.ChatColor.*
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.IllegalArgumentException
import java.util.*

class WarpPlugin : JavaPlugin() {
    private lateinit var noPermissionsForCommand: String
    private lateinit var noPermissionsForWarp: String
    private lateinit var noWarpForName: String

    private fun loadConfig() {
        config.options().copyDefaults(true)
        saveDefaultConfig()

        noPermissionsForCommand = ChatColor.translateAlternateColorCodes('&', config.getString("no-permissions-for-command")!!)
        noPermissionsForWarp = ChatColor.translateAlternateColorCodes('&', config.getString("no-permissions-for-warp")!!)
        noWarpForName = ChatColor.translateAlternateColorCodes('&', config.getString("no-warp-for-name")!!)
    }

    override fun onEnable() {
        instance = this

        WarpRegistry.load()

        loadConfig()

        getCommand("warps")?.setExecutor(this)
        getCommand("warp")?.setExecutor(this)
    }

    override fun onDisable() {
        WarpRegistry.unload()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when (label) {
            "warps" -> {
                if (args.size > 1) {
                    when (args[0]) {
                        // /warps create <name>
                        "create" -> {
                            if (!sender.hasPermission("warps.create")) {
                                sender.sendMessage(noPermissionsForCommand)
                                return true
                            }
                            if (args.size != 2) {
                                sender.sendMessage("${RED}Invalid number of args, usage is: /warps create <name>")
                                return true
                            }
                            if (sender !is Player) {
                                sender.sendMessage("${RED}Only players can run this command.")
                                return true
                            }
                            val id = args[1]
                            val old = WarpRegistry.register(Warp(id).also { it.location = sender.location })
                            sender.sendMessage("${GREEN}Warp $YELLOW$name $GREEN" + if (old == null) "created." else "replaced.")
                            return true
                        }

                        // /warps setsound <name> <sound>
                        "setsound" -> {
                            if (!sender.hasPermission("warps.setsound")) {
                                sender.sendMessage(noPermissionsForCommand)
                                return true
                            }
                            if (args.size != 3) {
                                sender.sendMessage("${RED}Invalid number of args, usage is: /warps setsound <name> <sound>")
                                return true
                            }
                            val warp = WarpRegistry.get(args[1])
                            if (warp == null) {
                                sender.sendMessage("${RED}No warp for: ${args[1]}.")
                                return true
                            }
                            val sound: Sound?
                            try {
                                sound = Sound.valueOf(args[2])
                            } catch (ignored: IllegalArgumentException) {
                                sender.sendMessage("${RED}Invalid sound: ${args[2]}.")
                                return true
                            }
                            sender.sendMessage("${GREEN}Sound $YELLOW${sound.name} ${GREEN}attached to $YELLOW${warp.id} $GREEN.")
                            return true
                        }

                        // /warps setmessage <name> <sound>
                        "setmessage" -> {
                            if (!sender.hasPermission("warps.message")) {
                                sender.sendMessage(noPermissionsForCommand)
                                return true
                            }
                            if (args.size != 3) {
                                sender.sendMessage("${RED}Invalid number of args, usage is: /warps setmessage <name> <message>")
                                return true
                            }
                            val warp = WarpRegistry.get(args[1])
                            if (warp == null) {
                                sender.sendMessage("${RED}No warp for: ${args[1]}.")
                                return true
                            }
                            warp.message = Collections.singletonList(args[2])
                            sender.sendMessage("${GREEN}Message attached to the warp: $YELLOW${warp.id}$GREEN. " +
                                    "If you'd like to add a longer message, consider editing it in configuration file.")
                            return true
                        }

                        // /warps delete <name>
                        "delete", "del" -> {
                            if (!sender.hasPermission("warps.delete")) {
                                sender.sendMessage(noPermissionsForCommand)
                                return true
                            }
                            if (args.size != 2) {
                                sender.sendMessage("${RED}Invalid number of args, usage is: /warps delete <name>")
                                return true
                            }
                            val id = args[1]
                            if (WarpRegistry.unregister(id) == null) {
                                sender.sendMessage("${RED}Warp not found for: $id.")
                                return true
                            }
                            sender.sendMessage("${GREEN}Warp $YELLOW$id ${GREEN}deleted.")
                            return true
                        }

                        else -> {
                            sender.sendMessage("${RED}No sub-command for: ${args[1]}.")
                            return true
                        }
                    }
                } else {
                    // /warps
                    if (!sender.hasPermission("warps.list")) {
                        sender.sendMessage(noPermissionsForCommand)
                        return true
                    }
                    val warps = WarpRegistry.list()
                    sender.sendMessage("${GREEN}There are $YELLOW${warps.size} $GREEN:")
                    for (warp in warps) {
                        sender.sendMessage("$GREEN- $YELLOW${warp.id}")
                    }
                    return true
                }
            }

            // /warp <name>
            "warp" -> {
                if (!sender.hasPermission("warps.warp")) {
                    sender.sendMessage(noPermissionsForCommand)
                    return true
                }
                if (sender !is Player) {
                    sender.sendMessage("${RED}Only players can run this command.")
                    return true
                }
                val warp = WarpRegistry.get(args[0])
                if (warp == null) {
                    sender.sendMessage(noWarpForName)
                    return true
                }
                if (!warp.execute(sender)) {
                    sender.sendMessage(noPermissionsForWarp)
                }
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {
        private var instance: WarpPlugin? = null

        fun get(): WarpPlugin {
            return instance!!
        }
    }
}
