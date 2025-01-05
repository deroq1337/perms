package com.github.deroq1337.perms.bukkit.data.listeners;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {

    private final @NotNull PermsPlugin plugin;

    public PlayerQuitListener(@NotNull PermsPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getUserRegistry().removeUser(event.getPlayer().getUniqueId());
    }
}
