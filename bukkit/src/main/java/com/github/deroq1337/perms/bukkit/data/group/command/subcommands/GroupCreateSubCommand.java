package com.github.deroq1337.perms.bukkit.data.group.command.subcommands;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.bukkit.data.group.entity.Group;
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
        if (args.length < 5) {
            commandSender.sendMessage("§c/group create <id> <name> <color> <prefix|null> <priority>");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (optionalGroup.isPresent()) {
                    commandSender.sendMessage("§cDiese Gruppe gibts bereits");
                    return;
                }

                int priority;
                try {
                    priority = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage("§cGib eine valide Zahl an!");
                    return;
                }

                String prefix = args[1].equals("null")
                        ? null
                        : args[1];
                Group group = new Group(groupId, args[1], new HashSet<>(), null, args[2], prefix, priority);

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