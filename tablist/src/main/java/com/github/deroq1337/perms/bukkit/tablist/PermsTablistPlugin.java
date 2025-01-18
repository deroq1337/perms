package com.github.deroq1337.perms.bukkit.tablist;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.tablist.listeners.PlayerJoinListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PermsTablistPlugin extends JavaPlugin {

    private PermsPlugin permsPlugin;
    private Tablist tablist;

    @Override
    public void onEnable() {
        this.permsPlugin = (PermsPlugin) Bukkit.getPluginManager().getPlugin("Perms");
        this.tablist = new Tablist(this);

        new PlayerJoinListener(this);
    }

    @Override
    public void onDisable() {

    }
}
