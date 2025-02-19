package com.github.deroq1337.perms.bukkit.tablist;

import com.github.deroq1337.perms.bukkit.data.group.entity.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Tablist {

    private final @NotNull PermsTablistPlugin plugin;
    private final @NotNull Scoreboard scoreboard;

    public Tablist(@NotNull PermsTablistPlugin plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void setTablist(@NotNull Player player) {
        String playerName = player.getName();

        Optional.ofNullable(Bukkit.getScoreboardManager()).ifPresent(scoreboardManager -> {
            plugin.getPermsPlugin().getUserManager().getGroup(player.getUniqueId()).thenCompose(userGroup -> {
                return plugin.getPermsPlugin().getGroupManager().getGroupById(userGroup.groupId()).thenAccept(optionalGroup -> {
                    optionalGroup.ifPresentOrElse(group -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Team team = getTeam(getTeamName(group));
                            team.setPrefix(getPrefix(group));
                            team.setColor(getColorAsEnum(group));
                            team.addEntry(player.getName());

                            player.setScoreboard(scoreboard);
                        });
                    }, () -> {
                        throw new RuntimeException("user '" + playerName + "' has no group");
                    });
                });
            });
        });
    }

    private @NotNull String getTeamName(@NotNull Group group) {
        return String.format("%03d_%s", group.getPriority(), group.getId());
    }

    private @NotNull String getPrefix(@NotNull Group group) {
        String prefix = Optional.ofNullable(group.getPrefix())
                .map(groupPrefix -> group.getColor() + groupPrefix + " §7| ")
                .orElse("");

        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    private @NotNull Team getTeam(@NotNull String name) {
        return Optional.ofNullable(scoreboard.getTeam(name))
                .orElseGet(() -> scoreboard.registerNewTeam(name));
    }

    private @NotNull ChatColor getColorAsEnum(@NotNull Group group) {
        return Optional.ofNullable(ChatColor.getByChar(group.getColor().charAt(1)))
                .orElse(ChatColor.WHITE);
    }
}
