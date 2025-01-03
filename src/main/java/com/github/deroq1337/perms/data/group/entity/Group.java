package com.github.deroq1337.perms.data.group.entity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record Group(@NotNull String id, @NotNull String name, @NotNull List<String> permissions, Optional<List<String>> inheritances,
                    @NotNull String color, Optional<String> prefix) {
}
