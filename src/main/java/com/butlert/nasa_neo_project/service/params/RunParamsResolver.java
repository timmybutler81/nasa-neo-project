package com.butlert.nasa_neo_project.service.params;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class RunParamsResolver {

    public RunParams resolveFromEnv() {
        String runMode = System.getenv("RUN_MODE");
        if (runMode == null || runMode.isBlank()) {
            throw new IllegalStateException("RUN_MODE environment variable is missing");
        }

        if (Objects.equals(runMode, "DAILY")) {
            String requested = System.getenv("REQUESTED_DATE");
            if (requested == null || requested.isBlank()) {
                throw new IllegalStateException("REQUESTED_DATE environment variable is missing for DAILY mode");
            }
            LocalDate d = LocalDate.parse(requested);
            return new RunParams(runMode, d, d);
        }

        if (Objects.equals(runMode, "BACKFILL")) {
            String start = System.getenv("BACKFILL_START");
            String end = System.getenv("BACKFILL_END");
            if (start == null || start.isBlank() || end == null || end.isBlank()) {
                throw new IllegalStateException("BACKFILL_START/BACKFILL_END environment variables are missing for BACKFILL mode");
            }
            LocalDate s = LocalDate.parse(start);
            LocalDate e = LocalDate.parse(end);
            return new RunParams(runMode, s, e);
        }

        throw new IllegalStateException("Unknown RUN_MODE: " + runMode);
    }

    public RunParams resolveForDaily(LocalDate requestedDate) {
        if (requestedDate == null) {
            throw new IllegalArgumentException("requestedDate cannot be null");
        }
        return new RunParams("DAILY", requestedDate, requestedDate);
    }

    public RunParams resolveForBackfill(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate/endDate cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }
        return new RunParams("BACKFILL", startDate, endDate);
    }
}
