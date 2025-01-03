package com.github.deroq1337.perms.data.group.command.subcommands;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.command.GroupSubCommand;
import com.github.deroq1337.perms.data.group.entity.Group;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;

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
        System.out.println("1");
        groupManager.getGroupById(groupId).thenAccept(optionalGroup -> {
            System.out.println(optionalGroup);
            if (optionalGroup.isPresent()) {
                commandSender.sendMessage("§cDiese Gruppe gibts bereits");
                return;
            }

            Optional<String> prefix = (args.length > 3
                    ? Optional.of(args[3])
                    : Optional.empty());
            Group group = new Group(groupId, args[1], Collections.emptyList(), Optional.empty(), args[2], prefix);

            groupManager.createGroup(group).thenAccept(created -> {
                if (created) {
                    commandSender.sendMessage("§aGruppe wurde erstellt");
                } else {
                    commandSender.sendMessage("§cGruppe konnte nicht erstellt werden. Siehe Server-Logs oder Cassandra-Logs");
                }
            });
        });
    }
}