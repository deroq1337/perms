package com.github.deroq1337.perms.bukkit.data.user.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum Duration {

    DAYS('d', 24 * 60 * 60 * 1000L),
    WEEKS('w', 7 * DAYS.getMillis()),
    MONTHS('m', 30 * WEEKS.getMillis()),
    YEARS('y', 12 * MONTHS.getMillis());

    private final char unitChar;
    private final long millis;

    public static Optional<Duration> getDurationByChar(char c) {
        return Arrays.stream(values())
                .filter(duration -> duration.getUnitChar() == c)
                .findFirst();
    }
}
