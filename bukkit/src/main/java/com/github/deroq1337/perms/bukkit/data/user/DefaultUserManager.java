package com.github.deroq1337.perms.bukkit.data.user;

import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.group.GroupManager;
import com.github.deroq1337.perms.bukkit.data.group.entity.Group;
import com.github.deroq1337.perms.bukkit.data.user.entity.UserGroup;
import com.github.deroq1337.perms.bukkit.data.user.repository.DefaultUserRepository;
import com.github.deroq1337.perms.bukkit.data.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultUserManager implements UserManager {

    private static final String DEFAULT_GROUP_ID = "default";

    private final @NotNull UserRepository repository;
    private final @NotNull GroupManager groupManager;

    public DefaultUserManager(@NotNull PermsPlugin plugin) {
        this.repository = new DefaultUserRepository(plugin);
        this.groupManager = plugin.getGroupManager();
    }

    @Override
    public @NotNull CompletableFuture<Boolean> setGroup(@NotNull UserGroup user) {
        return repository.setGroup(user);
    }

    @Override
    public @NotNull CompletableFuture<UserGroup> getGroup(@NotNull UUID player) {
        return repository.getGroup(player).thenApply(optionalUserGroup ->
                optionalUserGroup.orElseGet(() -> new UserGroup(player, DEFAULT_GROUP_ID, -1, -1, -1)));
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> getPermissions(@NotNull UUID player) {
        return repository.getGroup(player).thenCompose(optionalUserGroup -> {
            String groupId = optionalUserGroup.
                    map(UserGroup::groupId)
                    .orElse(DEFAULT_GROUP_ID);

            return groupManager.getGroupById(groupId).thenCompose(optionalGroup -> {
                Group group = optionalGroup.orElseGet(() ->
                        groupManager.getGroupById(DEFAULT_GROUP_ID).join().orElseThrow(() -> new NoSuchElementException("default group was not found")));

                if (group.getId().equals(DEFAULT_GROUP_ID) || group.getInheritance() == null) {
                    return CompletableFuture.completedFuture(group.getPermissions());
                }

                Set<String> permissions = new HashSet<>(group.getPermissions());
                return loadInheritedPermissions(group).thenApply(inheritedPermissions -> {
                    permissions.addAll(inheritedPermissions);
                    return permissions;
                });
            });
        });
    }

    private @NotNull CompletableFuture<Set<String>> loadInheritedPermissions(@NotNull Group group) {
        Set<String> inheritedPermissions = new HashSet<>();
        AtomicReference<String> inheritedGroupId = new AtomicReference<>(group.getInheritance());
        CompletableFuture<Void> futureChain = CompletableFuture.completedFuture(null);

        while (inheritedGroupId.get() != null) {
            futureChain = futureChain.thenCompose(v -> {
                return groupManager.getGroupById(inheritedGroupId.get()).thenAccept(optionalInheritedGroup -> {
                    optionalInheritedGroup.ifPresentOrElse(inheritedGroup -> {
                        inheritedPermissions.addAll(inheritedGroup.getPermissions());
                        inheritedGroupId.set(inheritedGroup.getInheritance());
                    }, () -> inheritedGroupId.set(null));
                });
            });
        }

        return futureChain.thenApply(v -> inheritedPermissions);
    }
}