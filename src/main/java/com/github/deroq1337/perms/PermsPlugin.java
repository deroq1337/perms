package com.github.deroq1337.perms;

import com.github.deroq1337.perms.data.database.Cassandra;
import com.github.deroq1337.perms.data.group.DefaultGroupManager;
import com.github.deroq1337.perms.data.group.GroupManager;
import com.github.deroq1337.perms.data.group.command.GroupCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PermsPlugin extends JavaPlugin {

    private Cassandra cassandra;
    private GroupManager groupManager;

    @Override
    public void onEnable() {
        this.cassandra = new Cassandra();
        cassandra.connect();

        this.groupManager = new DefaultGroupManager(this);

        new GroupCommand(this);
    }

    @Override
    public void onDisable() {
        cassandra.disconnect();
    }
}
