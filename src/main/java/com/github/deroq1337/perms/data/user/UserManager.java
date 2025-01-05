package com.github.deroq1337.perms.data.user;

import com.github.deroq1337.perms.data.user.entity.UserGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    @NotNull CompletableFuture<Boolean> setGroup(@NotNull UserGroup userGroup);

    @NotNull CompletableFuture<Optional<UserGroup>> getGroup(@NotNull UUID player);

    @NotNull CompletableFuture<Set<String>> getPermissions(@NotNull UUID player);
}
