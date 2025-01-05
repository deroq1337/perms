package com.github.deroq1337.perms.bukkit.data.user.repository;

import com.github.deroq1337.perms.bukkit.data.user.entity.UserGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    @NotNull CompletableFuture<Boolean> setGroup(@NotNull UserGroup user);

    @NotNull CompletableFuture<Optional<UserGroup>> getGroup(@NotNull UUID player);
}
