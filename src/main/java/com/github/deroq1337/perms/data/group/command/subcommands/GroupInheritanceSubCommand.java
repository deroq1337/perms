package com.github.deroq1337.perms.data.group.command.subcommands;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.data.group.entity.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GroupInheritanceSubCommand extends GroupSubCommand {

    public GroupInheritanceSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "inheritance");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage("§c/group inheritance <id> add|remove <groupId>");
            return;
        }

        String operation = args[1].toLowerCase();
        if (!operation.equals("add") && !operation.equals("remove")) {
            commandSender.sendMessage("§c/group inheritance <id> add|remove <groupId>");
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
                Set<String> inheritances = group.getInheritances();
                if (operation.equals("remove") && (inheritances == null || inheritances.isEmpty())) {
                    commandSender.sendMessage("§cDiese Gruppe hat noch keine Erbschaften");
                    return;
                }

                String inheritanceGroupId = args[2];
                if (operation.equals("remove")) {
                    if (!inheritances.remove(inheritanceGroupId)) {
                        commandSender.sendMessage("§cDiese Gruppe erbt nicht von " + inheritanceGroupId);
                        return;
                    }
                } else {
                    if (inheritances == null) {
                        inheritances = new HashSet<>();
                    }

                    if (!inheritances.add(inheritanceGroupId)) {
                        commandSender.sendMessage("§cDiese Gruppe erbt bereits von " + inheritanceGroupId);
                        return;
                    }
                }

                group.setInheritances(inheritances);
                groupManager.updateGroup(group).thenAccept(created -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (created) {
                            commandSender.sendMessage("§aErbschaften wurden aktualisiert");
                        } else {
                            commandSender.sendMessage("§cGruppe konnte nicht aktualisiert werden. Siehe Server-Logs oder Cassandra-Logs");
                        }
                    });
                });
            });
        });
    }
}