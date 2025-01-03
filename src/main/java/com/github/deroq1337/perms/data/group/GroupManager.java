package com.github.deroq1337.perms.data.group;

import com.github.deroq1337.perms.data.group.entity.Group;
import com.google.common.cache.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GroupManager {

    @NotNull CompletableFuture<Boolean> createGroup(@NotNull Group group);

    @NotNull CompletableFuture<Boolean> updateGroup(@NotNull Group group);

    @NotNull CompletableFuture<Boolean> deleteGroup(@NotNull String id);

    @NotNull CompletableFuture<Optional<Group>> getGroupById(@NotNull String id);

    @NotNull CompletableFuture<Set<Group>> getGroupsByPlayer(@NotNull UUID player);

    @NotNull CompletableFuture<Set<Group>> getGroups();

    @NotNull Cache<String, CompletableFuture<Optional<Group>>> getGroupCache();
}
