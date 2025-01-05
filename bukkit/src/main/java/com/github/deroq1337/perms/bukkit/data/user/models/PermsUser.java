package com.github.deroq1337.perms.bukkit.data.user.models;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

@Getter
public class PermsUser {

    private final @NotNull UUID uuid;
    private final @NotNull Set<String> permissions;
    private final @NotNull PermissionAttachment permissionAttachment;

    public PermsUser(@NotNull Player player, @NotNull Set<String> permissions) {
        this.uuid = player.getUniqueId();
        this.permissions = permissions;
        this.permissionAttachment = player.addAttachment(JavaPlugin.getPlugin(PermsPlugin.class));

        permissions.forEach(permission -> permissionAttachment.setPermission(permission, true));
    }

    public void unsetPermissions() {
        permissions.forEach(permissionAttachment::unsetPermission);
    }
}
