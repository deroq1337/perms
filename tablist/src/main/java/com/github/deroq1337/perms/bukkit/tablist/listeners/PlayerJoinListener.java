package com.github.deroq1337.perms.bukkit.tablist.listeners;

import com.github.deroq1337.perms.bukkit.tablist.PermsTablistPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    private final @NotNull PermsTablistPlugin plugin;

    public PlayerJoinListener(@NotNull PermsTablistPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getTablist().setTablist(event.getPlayer());
    }
}
