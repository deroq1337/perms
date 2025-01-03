package com.github.deroq1337.perms.data.group.repository;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.github.deroq1337.perms.PermsPlugin;
import com.github.deroq1337.perms.data.database.Cassandra;
import com.github.deroq1337.perms.data.group.entity.Group;
import com.github.deroq1337.perms.data.utils.ListenableFutureConverter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.Futures;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DefaultGroupRepository implements GroupRepository {

    private final @NotNull Session session;
    private final @NotNull PreparedStatement createGroup;
    private final @NotNull PreparedStatement getGroupById;
    private final @NotNull PreparedStatement getGroups;
    private final @NotNull PreparedStatement getGroupsByPlayer;

    public DefaultGroupRepository(@NotNull PermsPlugin plugin) {
        this.session = plugin.getCassandra().getSession();
        createTables();

        this.createGroup = session.prepare("INSERT INTO perms.groups" +
                "(id, name, permissions, color, prefix) " +
                "VALUES (?, ?, ?, ?, ?);");
        this.getGroupById = session.prepare("SELECT * " +
                "FROM perms.groups " +
                "WHERE id = ?;");
        this.getGroups = session.prepare("SELECT * " +
                "FROM perms.groups;");
        this.getGroupsByPlayer = session.prepare("SELECT group " +
                "FROM perms.user_groups " +
                "WHERE player = ?;");
    }

    private void createTables() {
        session.execute("CREATE TABLE IF NOT EXISTS perms.groups(" +
                "id VARCHAR," +
                "name VARCHAR," +
                "permissions LIST<VARCHAR>," +
                "inheritances LIST<VARCHAR>," +
                "color VARCHAR," +
                "prefix VARCHAR," +
                "PRIMARY KEY(id)" +
                ");");

        session.execute("CREATE TABLE IF NOT EXISTS perms.user_groups(" +
                "player UUID," +
                "group VARCHAR," +
                "PRIMARY KEY(player, group)" +
                ");");
    }

    @Override
    public @NotNull CompletableFuture<Boolean> createGroup(@NotNull Group group) {
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(
                session.executeAsync(createGroup.bind(group.id().toLowerCase(), group.name(), group.permissions(),
                        group.color(), group.prefix().orElse(null))),
                ResultSet::wasApplied,
                Cassandra.ASYNC_EXECUTOR)
        );
    }

    @Override
    public @NotNull CompletableFuture<Optional<Group>> getGroupById(@NotNull String id) {
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(session.executeAsync(getGroupById.bind(id.toLowerCase())), result -> {
            Iterator<Row> rows = result.iterator();
            if (!rows.hasNext()) {
                return Optional.empty();
            }

            return Optional.of(mapGroupFromRow(rows.next()));
        }, Cassandra.ASYNC_EXECUTOR));
    }

    @Override
    public @NotNull CompletableFuture<Set<Group>> getGroupsByPlayer(@NotNull UUID player) {
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(session.executeAsync(getGroupsByPlayer.bind(player)), result -> {
            return result.all().stream()
                    .map(row -> row.getString("group"))
                    .collect(Collectors.toSet());
        }, Cassandra.ASYNC_EXECUTOR)).thenCompose(groupIds -> {
            List<CompletableFuture<Optional<Group>>> groupFutures = groupIds.stream()
                    .map(this::getGroupById)
                    .toList();

            return CompletableFuture.allOf(groupFutures.toArray(new CompletableFuture[0])).thenApply(voidResult -> {
                return groupFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
            });
        });
    }

    @Override
    public @NotNull CompletableFuture<Set<Group>> getGroups() {
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(session.executeAsync(getGroups.bind()), result -> {
            return result.all().stream()
                    .map(this::mapGroupFromRow)
                    .collect(Collectors.toSet());
        }, Cassandra.ASYNC_EXECUTOR));
    }

    private @NotNull Group mapGroupFromRow(@NotNull Row row) {
        return new Group(
                row.getString("id"),
                row.getString("name"),
                row.getList("permissions", new TypeToken<>() {}),
                Optional.ofNullable(row.getList("inheritances", new TypeToken<>() {})),
                row.getString("color"),
                Optional.ofNullable(row.getString("prefix"))
        );
    }
}
