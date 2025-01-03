package com.github.deroq1337.perms.data.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cassandra {

    private static final PoolingOptions POOLING_OPTIONS = new PoolingOptions()
            .setConnectionsPerHost(HostDistance.LOCAL, 4, 10)
            .setConnectionsPerHost(HostDistance.REMOTE, 2, 4);

    public static final ExecutorService ASYNC_EXECUTOR = Executors.newFixedThreadPool(4);

    @Getter
    private Optional<Cluster> cluster = Optional.empty();
    private Optional<Session> session = Optional.empty();

    public void connect() {
        this.cluster = Optional.of(Cluster.builder()
                .addContactPoint("127.0.0.1")
                .withPort(9042)
                .withPoolingOptions(POOLING_OPTIONS)
                .build());
        cluster.ifPresent(cluster -> {
            this.session = Optional.of(cluster.connect());
            System.out.println("Connected to Cassandra");
        });
    }

    public void disconnect() {
        session.ifPresent(Session::close);
        cluster.ifPresent(cluster -> {
            cluster.close();
            System.out.println("Disconnected from Cassandra");
        });

        this.session = Optional.empty();
        this.cluster = Optional.empty();
    }

    public @NotNull Session getSession() {
        return session.orElseGet(() -> {
            Session newSession = cluster
                    .map(Cluster::newSession)
                    .orElseThrow(() -> new IllegalStateException("Could not create new session: No cluster connected"));
            this.session = Optional.of(newSession);
            return newSession;
        });
    }
}
