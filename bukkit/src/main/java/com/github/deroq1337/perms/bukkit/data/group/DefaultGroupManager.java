package com.github.deroq1337.perms.bukkit.data.group;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.entity.Group;
import com.github.deroq1337.perms.bukkit.data.group.repository.DefaultGroupRepository;
import com.github.deroq1337.perms.bukkit.data.group.repository.GroupRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DefaultGroupManager implements GroupManager {

    private final @NotNull GroupRepository repository;
    private final @NotNull LoadingCache<String, CompletableFuture<Optional<Group>>> idGroupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull CompletableFuture<Optional<Group>> load(@NotNull String key) {
                    return repository.findById(key);
                }
            });

    public DefaultGroupManager(@NotNull PermsPlugin plugin) {
        this.repository = new DefaultGroupRepository(plugin);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> createGroup(@NotNull Group group) {
        return repository.create(group);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> updateGroup(@NotNull Group group) {
        return repository.update(group).thenApply(success -> {
            if (success) {
                idGroupCache.invalidate(group.getId());
            }

            return success;
        });
    }

    @Override
    public @NotNull CompletableFuture<Boolean> deleteGroup(@NotNull String id) {
        return repository.deleteById(id).thenApply(success -> {
            if (success) {
                idGroupCache.invalidate(id);
            }

            return success;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<Group>> getGroupById(@NotNull String id) {
        return idGroupCache.getUnchecked(id);
    }

    @Override
    public @NotNull CompletableFuture<List<Group>> getGroups() {
        return repository.findAll();
    }

    @Override
    public @NotNull Cache<String, CompletableFuture<Optional<Group>>> getGroupCache() {
        return idGroupCache;
    }
}
