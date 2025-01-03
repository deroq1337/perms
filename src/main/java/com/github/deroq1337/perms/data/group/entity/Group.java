package com.github.deroq1337.perms.data.group.entity;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Group {

    private final @NotNull String id;
    private @NotNull String name;
    private @NotNull Set<String> permissions;
    private @Nullable Set<String> inheritances;
    private @NotNull String color;
    private @Nullable String prefix;
    private int priority;
}
