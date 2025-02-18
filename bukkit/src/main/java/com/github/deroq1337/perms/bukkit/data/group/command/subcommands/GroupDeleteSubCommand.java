package com.github.deroq1337.perms.bukkit.data.group.command.subcommands;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.command.GroupSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GroupDeleteSubCommand extends GroupSubCommand {

    public GroupDeleteSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "delete");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage("§c/group delete <id>");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                optionalGroup.ifPresentOrElse(group -> {
                    groupManager.deleteGroup(groupId).thenAccept(success -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (!success) {
                                commandSender.sendMessage("§cGruppe konnte nicht gelöscht werden. Siehe Server-Logs oder Cassandra-Logs");
                                return;
                            }

                            commandSender.sendMessage("§aGruppe wurde gelöscht");
                        });
                    });
                }, () -> commandSender.sendMessage("§cDiese Gruppe gibt es nicht"));
            });
        });
    }
}