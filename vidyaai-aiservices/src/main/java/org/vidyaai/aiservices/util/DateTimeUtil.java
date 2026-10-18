package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    public String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return ISO_FORMATTER.format(dateTime);
        } catch (Exception e) {
            log.warn("Failed to format dateTime: {}", e.getMessage());
            return null;
        }
    }

    public LocalDateTime parseIso(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, ISO_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse ISO dateTime: {}", e.getMessage());
            return null;
        }
    }

    public long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.SECONDS.between(start, end);
    }

    public long millisBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0L;
        }
        return ChronoUnit.MILLIS.between(start, end);
    }

    public boolean isOlderThanDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(now().minusDays(days));
    }
}