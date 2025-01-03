package com.github.deroq1337.perms.data.group.command.subcommands;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.data.group.entity.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class GroupCreateSubCommand extends GroupSubCommand {

    public GroupCreateSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "create");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage("§c/group create <id*> <name*> <color*> <prefix>");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (optionalGroup.isPresent()) {
                    commandSender.sendMessage("§cDiese Gruppe gibts bereits");
                    return;
                }

                String prefix = (args.length > 3
                        ? args[3]
                        : null);
                Group group = new Group(groupId, args[1], new HashSet<>(), null, args[2], prefix);

                groupManager.createGroup(group).thenAccept(created -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (created) {
                            commandSender.sendMessage("§aGruppe wurde erstellt");
                        } else {
                            commandSender.sendMessage("§cGruppe konnte nicht erstellt werden. Siehe Server-Logs oder Cassandra-Logs");
                        }
                    });
                });
            });
        });
    }
}