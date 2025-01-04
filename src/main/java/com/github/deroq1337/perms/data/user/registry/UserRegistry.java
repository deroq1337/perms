package com.github.deroq1337.perms.data.user.registry;

import com.github.deroq1337.perms.data.user.models.PermsUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserRegistry {

    private final @NotNull Map<UUID, PermsUser> userMap = new HashMap<>();

    public void addUser(@NotNull Player player, @NotNull Set<String> permissions) {
        userMap.put(player.getUniqueId(), new PermsUser(player, permissions));
    }

    public void removeUser(@NotNull UUID uuid) {
        Optional.ofNullable(userMap.get(uuid)).ifPresent(PermsUser::unsetPermissions);
        userMap.remove(uuid);
    }
}
