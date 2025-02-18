package com.github.deroq1337.perms.bukkit.data.group.command.subcommands;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.command.GroupSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GroupSetPrioritySubCommand extends GroupSubCommand {

    public GroupSetPrioritySubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "setPriority");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("§c/group setPriority <id> <priority>");
            return;
        }

        int priority;
        try {
            priority = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage("§cGib eine valide Zahl an!");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                optionalGroup.ifPresentOrElse(group -> {
                    group.setPriority(priority);

                    groupManager.updateGroup(group).thenAccept(success -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (!success) {
                                commandSender.sendMessage("§cGruppe konnte nicht gelöscht werden. Siehe Server-Logs oder Cassandra-Logs");
                                return;
                            }

                            commandSender.sendMessage("§aName wurde aktualisiert");
                        });
                    });
                }, () -> commandSender.sendMessage("§cDiese Gruppe gibt es nicht"));
            });
        });
    }
}