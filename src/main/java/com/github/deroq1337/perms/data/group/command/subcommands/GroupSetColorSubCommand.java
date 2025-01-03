package com.github.deroq1337.perms.data.group.command.subcommands;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.data.group.entity.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GroupSetColorSubCommand extends GroupSubCommand {

    public GroupSetColorSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "setColor");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("§c/group setColor <id> <color>");
            return;
        }

        String groupId = args[0];
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (optionalGroup.isEmpty()) {
                    commandSender.sendMessage("§cDiese Gruppe gibt es nicht");
                    return;
                }

                Group group = optionalGroup.get();
                group.setColor(args[1]);

                groupManager.updateGroup(group).thenAccept(updated -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (updated) {
                            commandSender.sendMessage("§aFarbe wurde aktualisiert");
                        } else {
                            commandSender.sendMessage("§cGruppe konnte nicht gelöscht werden. Siehe Server-Logs oder Cassandra-Logs");
                        }
                    });
                });
            });
        });
    }
}