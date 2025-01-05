package com.github.deroq1337.perms.bukkit.data.user.repository;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.github.deroq1337.perms.bukkit.PermsPlugin;
import com.github.deroq1337.perms.bukkit.data.database.Cassandra;
import com.github.deroq1337.perms.bukkit.data.user.entity.UserGroup;
import com.github.deroq1337.perms.bukkit.data.utils.ListenableFutureConverter;
import com.google.common.util.concurrent.Futures;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultUserRepository implements UserRepository {

    private final @NotNull Session session;
    private final @NotNull PreparedStatement setGroup;
    private final @NotNull PreparedStatement getGroup;

    public DefaultUserRepository(@NotNull PermsPlugin plugin) {
        this.session = plugin.getCassandra().getSession();
        createTableAndIndex();

        this.setGroup = session.prepare("INSERT INTO perms.user_groups" +
                "(player, group, given_at, duration, expires_at)" +
                "VALUES (?, ?, ?, ?, ?) " +
                "USING TTL ?;");
        this.getGroup = session.prepare("SELECT * " +
                "FROM perms.user_groups " +
                "WHERE player = ?;");
    }

    private void createTableAndIndex() {
        session.execute("CREATE TABLE IF NOT EXISTS perms.user_groups(" +
                "player UUID," +
                "group VARCHAR," +
                "given_at BIGINT," +
                "duration BIGINT," +
                "expires_at BIGINT," +
                "PRIMARY KEY(player)" +
                ");");
    }

    @Override
    public @NotNull CompletableFuture<Boolean> setGroup(@NotNull UserGroup userGroup) {
        long ttlExpiry = userGroup.expiresAt() / 1000;
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(
                session.executeAsync(setGroup.bind(userGroup.player(), userGroup.groupId(), userGroup.givenAt(), userGroup.duration(), userGroup.expiresAt(), ttlExpiry)),
                ResultSet::wasApplied,
                Cassandra.ASYNC_EXECUTOR)
        );
    }

    @Override
    public @NotNull CompletableFuture<Optional<UserGroup>> getGroup(@NotNull UUID player) {
        return ListenableFutureConverter.toCompletableFuture(Futures.transform(session.executeAsync(getGroup.bind(player)), result -> {
            Iterator<Row> rows = result.iterator();
            if (!rows.hasNext()) {
                return Optional.empty();
            }

            return Optional.of(mapUserGroupFromRow(rows.next()));
        }, Cassandra.ASYNC_EXECUTOR));
    }

    private @NotNull UserGroup mapUserGroupFromRow(@NotNull Row row) {
        return new UserGroup(
                row.getUUID("player"),
                row.getString("group"),
                row.getLong("given_at"),
                row.getLong("duration"),
                row.getLong("expires_at")
        );
    }
}
