package com.github.deroq1337.perms.bukkit.data.group.command.subcommands;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.command.GroupSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GroupSetInheritanceSubCommand extends GroupSubCommand {

    public GroupSetInheritanceSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "inheritance");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("§c/group setInheritance <id> <inheritance>");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                optionalGroup.ifPresentOrElse(group -> {
                    group.setInheritance(args[1].equals("null") ? null : args[1]);

                    groupManager.updateGroup(group).thenAccept(updated -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            if (!updated) {
                                commandSender.sendMessage("§cGruppe konnte nicht aktualisiert werden. Siehe Server-Logs oder Cassandra-Logs");
                                return;
                            }

                            commandSender.sendMessage("§aErbschaft wurden aktualisiert");
                        });
                    });
                }, () -> commandSender.sendMessage("§cDiese Gruppe gibt es nicht"));
            });
        });
    }
}