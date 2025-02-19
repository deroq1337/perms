package com.github.deroq1337.perms.bukkit;

import com.github.deroq1337.perms.bukkit.data.database.Cassandra;
import com.github.deroq1337.perms.bukkit.data.group.DefaultGroupManager;
import com.github.deroq1337.perms.bukkit.data.group.GroupManager;
import com.github.deroq1337.perms.bukkit.data.group.command.GroupCommand;
import com.github.deroq1337.perms.bukkit.data.listeners.PlayerJoinListener;
import com.github.deroq1337.perms.bukkit.data.listeners.PlayerQuitListener;
import com.github.deroq1337.perms.bukkit.data.user.DefaultUserManager;
import com.github.deroq1337.perms.bukkit.data.user.UserManager;
import com.github.deroq1337.perms.bukkit.data.user.commands.SetGroupCommand;
import com.github.deroq1337.perms.bukkit.data.user.registry.UserRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PermsPlugin extends JavaPlugin {

    private Cassandra cassandra;
    private GroupManager groupManager;
    private UserManager userManager;
    private UserRegistry userRegistry;

    @Override
    public void onEnable() {
        this.cassandra = new Cassandra();
        cassandra.connect();

        this.groupManager = new DefaultGroupManager(this);
        this.userManager = new DefaultUserManager(this);
        this.userRegistry = new UserRegistry();

        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new GroupCommand(this);
        new SetGroupCommand(this);
    }

    @Override
    public void onDisable() {
        cassandra.disconnect();
    }
}
