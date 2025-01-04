package com.github.deroq1337.perms.data.user;

import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.group.GroupManager;
import com.github.deroq1337.perms.data.group.entity.Group;
import com.github.deroq1337.perms.data.user.entity.UserGroup;
import com.github.deroq1337.perms.data.user.repository.DefaultUserRepository;
import com.github.deroq1337.perms.data.user.repository.UserRepository;
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
    public @NotNull CompletableFuture<Optional<UserGroup>> getGroup(@NotNull UUID player) {
        return repository.getGroup(player);
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
        CompletableFuture<Void> futureChain = CompletableFuture.completedFuture(null);

        AtomicReference<String> inheritedGroupId = new AtomicReference<>(group.getInheritance());
        while (inheritedGroupId.get() != null) {
            futureChain = futureChain.thenCompose(v -> groupManager.getGroupById(inheritedGroupId.get()).thenAccept(optionalGroup -> {
                if (optionalGroup.isEmpty()) {
                    inheritedGroupId.set(null);
                    return;
                }

                Group inheritedGroup = optionalGroup.get();
                inheritedPermissions.addAll(inheritedGroup.getPermissions());
                inheritedGroupId.set(inheritedGroup.getInheritance());
            }));
        }

        return futureChain.thenApply(v -> inheritedPermissions);
    }
}