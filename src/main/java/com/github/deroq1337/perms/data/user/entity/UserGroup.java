package com.github.deroq1337.perms.data.user.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record UserGroup(@NotNull UUID player, @NotNull String groupId, long givenAt, long duration, long expiresAt) {
}
