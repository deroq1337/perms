package com.github.deroq1337.perms.bukkit.data.user;

import com.github.deroq1337.perms.bukkit.data.user.entity.UserGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    @NotNull CompletableFuture<Boolean> setGroup(@NotNull UserGroup userGroup);

    @NotNull CompletableFuture<UserGroup> getGroup(@NotNull UUID player);

    @NotNull CompletableFuture<Set<String>> getPermissions(@NotNull UUID player);
}
