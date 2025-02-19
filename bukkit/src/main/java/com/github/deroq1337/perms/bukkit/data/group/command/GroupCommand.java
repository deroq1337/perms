package com.github.deroq1337.perms.bukkit.data.group.command;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.command.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupCommand implements CommandExecutor {

    private final @NotNull Map<String, GroupSubCommand> subCommandMap;

    public GroupCommand(@NotNull PermsPlugin plugin) {
        this.subCommandMap = Stream.of(
                new GroupCreateSubCommand(plugin),
                new GroupDeleteSubCommand(plugin),
                new GroupPermissionSubCommand(plugin),
                new GroupSetInheritanceSubCommand(plugin),
                new GroupSetPrefixSubCommand(plugin),
                new GroupSetColorSubCommand(plugin),
                new GroupSetPrioritySubCommand(plugin),
                new GroupSetNameSubCommand(plugin)
        ).collect(Collectors.toMap(subCommand -> subCommand.getName().toLowerCase(), subCommand -> subCommand));

        Optional.ofNullable(plugin.getCommand("group")).ifPresent(command -> command.setExecutor(this));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("perms.group")) {
            commandSender.sendMessage("§cKeine Rechte!");
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage("§cBefehl nicht gefunden!");
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        Optional.ofNullable(subCommandMap.get(subCommandName)).ifPresentOrElse(subCommand -> {
            subCommand.execute(commandSender, buildSubCommandArgs(args));
        }, () -> commandSender.sendMessage("§cBefehl nicht gefunden!"));

        return true;
    }

    private @NotNull String[] buildSubCommandArgs(@NotNull String[] args) {
        return Arrays.stream(args)
                .skip(1)
                .toArray(String[]::new);
    }
}
