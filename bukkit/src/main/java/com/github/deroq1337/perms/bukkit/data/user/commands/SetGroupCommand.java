package com.github.deroq1337.perms.bukkit.data.user.commands;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.user.entity.UserGroup;
import com.github.deroq1337.perms.bukkit.data.user.utils.Duration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetGroupCommand implements CommandExecutor {

    private final @NotNull PermsPlugin plugin;

    public SetGroupCommand(@NotNull PermsPlugin plugin) {
        this.plugin = plugin;
        Optional.ofNullable(plugin.getCommand("setgroup")).ifPresent(command -> command.setExecutor(this));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("perms.setrank")) {
            commandSender.sendMessage("§cKeine Rechte!");
            return true;
        }

        if (args.length < 3) {
            commandSender.sendMessage("§c/setrank <player> <group> <duration>");
            return true;
        }

        UUID playerUuid; // working with UUIDs because no "cache" available
        try {
            playerUuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage("§cGib eine valide UUID an!");
            return true;
        }

        plugin.getGroupManager().getGroupById(args[1]).thenAccept(optionalGroup -> {
            if (optionalGroup.isEmpty()) {
                commandSender.sendMessage("§cDiese Gruppe gibt es nicht!");
                return;
            }

            Optional<Pair<Long, Long>> optionalDurationAndExpiry = parseDurationAndExpiry(args[2]);
            if (optionalDurationAndExpiry.isEmpty()) {
                commandSender.sendMessage("§cGib eine valide Dauer an!");
                return;
            }


            Pair<Long, Long> durationAndExpiry = optionalDurationAndExpiry.get();
            UserGroup userGroup = new UserGroup(playerUuid, optionalGroup.get().getId(), System.currentTimeMillis(),
                    durationAndExpiry.getLeft(), durationAndExpiry.getRight());
            plugin.getUserManager().setGroup(userGroup).thenAccept(set -> {
                if (set) {
                    commandSender.sendMessage("§aGruppe wurde gesetzt");
                } else {
                    commandSender.sendMessage("§cGruppe konnte nicht gesetzt werden. Check die Logs");
                }
            });
        });
        return true;
    }

    private Optional<Pair<Long, Long>> parseDurationAndExpiry(@NotNull String durationString) {
        Pattern pattern = Pattern.compile("^(\\d+)([a-z])$");
        Matcher matcher = pattern.matcher(durationString);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        int duration = Integer.parseInt(matcher.group(1));
        char unit = matcher.group(2).charAt(0);
        return Duration.getDurationByChar(unit)
                .map(d -> Pair.of(d.getMillis(), d.getMillis() * duration));
    }
}
