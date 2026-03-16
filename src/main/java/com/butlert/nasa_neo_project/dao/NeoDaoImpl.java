package com.butlert.nasa_neo_project.dao;

import com.butlert.nasa_neo_project.dto.CloseApproachDto;
import com.butlert.nasa_neo_project.dto.NeoDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public class NeoDaoImpl implements NeoDao {

    private final JdbcTemplate jdbcTemplate;

    public NeoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long upsertImportRunStart(LocalDate requestedDate) {
        String upsertSql = """
                INSERT INTO import_run (requested_date, status, error_message)
                            VALUES (?, 'FAILED', 'RUNNING')
                            ON DUPLICATE KEY UPDATE
                              run_timestamp = CURRENT_TIMESTAMP,
                              status = 'FAILED',
                              error_message = 'RUNNING'
                """;

        jdbcTemplate.update(upsertSql, requestedDate);

        String selectIdSql = """
            SELECT id
            FROM import_run
            WHERE requested_date = ?
            """;

        Long importRunId = jdbcTemplate.queryForObject(
                selectIdSql,
                Long.class,
                requestedDate
        );

        if (importRunId == null) {
            throw new IllegalStateException(
                    "Failed to retrieve import run id for date" + requestedDate
            );
        }

        return importRunId;

    }

    @Override
    public void upsertNeo(NeoDto neoDto) {
        String upsertNeoSql = """
                INSERT INTO neo (neo_id, name, is_potentially_hazardous, diameter_km_min, diameter_km_max)
                        VALUES (?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                        name = VALUES(name),
                                is_potentially_hazardous = VALUES(is_potentially_hazardous),
                                diameter_km_min = VALUES(diameter_km_min),
                                diameter_km_max = VALUES(diameter_km_max)
                """;

        jdbcTemplate.update(upsertNeoSql,
                neoDto.getNeoId(),
                neoDto.getName(),
                neoDto.isPotentiallyHazardous(),
                neoDto.getDiameterKmMin(),
                neoDto.getDiameterKmMax());


    }

    @Override
    public void markImportRunSuccess(long importRunId) {
        String updateRunIdSuccess = """
            UPDATE import_run
            SET status = 'SUCCESS', error_message = NULL
            WHERE id = ?
            """;

        jdbcTemplate.update(updateRunIdSuccess, importRunId);

    }

    @Override
    public void markImportRunFailed(long importRunId, String errorMessage) {
        String updateRunIdFail = """
            UPDATE import_run
            SET status = 'FAILED', error_message = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(updateRunIdFail, errorMessage, importRunId);

    }

    @Override
    public void batchUpsertCloseApproaches(List<CloseApproachDto> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO close_approach
                            (neo_id, approach_datetime, approach_date, orbiting_body, miss_distance_miles, relative_velocity_mph, import_run_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            approach_date = VALUES(approach_date),
                            miss_distance_miles = VALUES(miss_distance_miles),
                            relative_velocity_mph = VALUES(relative_velocity_mph),
                            import_run_id = VALUES(import_run_id)
                """;

        jdbcTemplate.batchUpdate(sql, items, 500, (ps, item) -> {
            ps.setString(1, item.getNeoId());
            ps.setTimestamp(2, Timestamp.valueOf(item.getApproachDateTime()));
            ps.setObject(3, item.getApproachDate());
            ps.setString(4, item.getOrbitingBody());
            ps.setBigDecimal(5, item.getMissDistanceMiles());
            ps.setBigDecimal(6, item.getRelativeVelocityMph());
            ps.setLong(7, item.getImportRunId());
        });
    }
}
