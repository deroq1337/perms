package com.github.deroq1337.perms.data.group;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.entity.Group;
import com.github.deroq1337.perms.data.group.repository.DefaultGroupRepository;
import com.github.deroq1337.perms.data.group.repository.GroupRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DefaultGroupManager implements GroupManager {

    private final @NotNull GroupRepository repository;
    private final @NotNull LoadingCache<String, CompletableFuture<Optional<Group>>> idGroupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull CompletableFuture<Optional<Group>> load(@NotNull String key) {
                    return getGroupById(key);
                }
            });

    public DefaultGroupManager(@NotNull PermsPlugin plugin) {
        this.repository = new DefaultGroupRepository(plugin);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> createGroup(@NotNull Group group) {
        return repository.createGroup(group);
    }

    @Override
    public @NotNull CompletableFuture<Optional<Group>> getGroupById(@NotNull String id) {
        return repository.getGroupById(id);
    }

    @Override
    public @NotNull CompletableFuture<Set<Group>> getGroupsByPlayer(@NotNull UUID player) {
        return repository.getGroupsByPlayer(player);
    }

    @Override
    public @NotNull CompletableFuture<Set<Group>> getGroups() {
        return repository.getGroups();
    }

    @Override
    public @NotNull Cache<String, CompletableFuture<Optional<Group>>> getGroupCache() {
        return idGroupCache;
    }
}
