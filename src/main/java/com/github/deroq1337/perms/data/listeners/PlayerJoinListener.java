package com.github.deroq1337.perms.data.listeners;

import com.github.deroq1337.perms.PermsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    private final @NotNull PermsPlugin plugin;

    public PlayerJoinListener(@NotNull PermsPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getUserManager().getPermissions(player.getUniqueId()).thenAccept(permissions -> {
            plugin.getUserRegistry().addUser(player, permissions);
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
