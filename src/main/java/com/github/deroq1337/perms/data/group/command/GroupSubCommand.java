package com.github.deroq1337.perms.data.group.command;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.GroupManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class GroupSubCommand {

    protected final @NotNull PermsPlugin plugin;
    protected final @NotNull GroupManager groupManager;
    private final @NotNull String name;

    public GroupSubCommand(@NotNull PermsPlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        this.groupManager = plugin.getGroupManager();
        this.name = name;
    }

    protected abstract void execute(@NotNull CommandSender commandSender, @NotNull String[] args);
}
