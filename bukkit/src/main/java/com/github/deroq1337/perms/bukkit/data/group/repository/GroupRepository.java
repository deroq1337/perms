package com.github.deroq1337.perms.bukkit.data.group.repository;

import com.github.deroq1337.perms.bukkit.data.group.entity.Group;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface GroupRepository {

    @NotNull CompletableFuture<Boolean> createGroup(@NotNull Group group);

    @NotNull CompletableFuture<Boolean> updateGroup(@NotNull Group group);

    @NotNull CompletableFuture<Boolean> deleteGroup(@NotNull String id);

    @NotNull CompletableFuture<Optional<Group>> getGroupById(@NotNull String id);

    @NotNull CompletableFuture<Set<Group>> getGroups();
}
