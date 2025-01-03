package com.github.deroq1337.perms.data.group.command.subcommands;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.data.group.entity.Group;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GroupPermissionSubCommand extends GroupSubCommand {

    public GroupPermissionSubCommand(@NotNull PermsPlugin plugin) {
        super(plugin, "permission");
    }

    @Override
    protected void execute(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage("§c/group permission <id> add|remove <permission>");
            return;
        }

        String operation = args[1].toLowerCase();
        if (!operation.equals("add") && !operation.equals("remove")) {
            commandSender.sendMessage("§c/group permission <id> add|remove <permission>");
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
                Set<String> permissions = group.getPermissions();
                if (operation.equals("remove") && permissions.isEmpty()) {
                    commandSender.sendMessage("§cDiese Gruppe hat noch keine Permissions");
                    return;
                }

                String permission = args[2];
                if (operation.equals("remove")) {
                    if (!permissions.remove(permission)) {
                        commandSender.sendMessage("§cDiese Gruppe hat diese Permission nicht");
                        return;
                    }
                } else {
                    if (!permissions.add(permission)) {
                        commandSender.sendMessage("§cDiese Gruppe hat bereits diese Permission");
                        return;
                    }
                }

                group.setPermissions(permissions);
                groupManager.updateGroup(group).thenAccept(updated -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (updated) {
                            commandSender.sendMessage("§aPermissions wurden aktualisiert");
                        } else {
                            commandSender.sendMessage("§cGruppe konnte nicht aktualisiert werden. Siehe Server-Logs oder Cassandra-Logs");
                        }
                    });
                });
            });
        });
    }
}