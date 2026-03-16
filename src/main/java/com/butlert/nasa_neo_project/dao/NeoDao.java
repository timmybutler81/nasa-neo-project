package com.butlert.nasa_neo_project.dao;

import com.butlert.nasa_neo_project.dto.CloseApproachDto;
import com.butlert.nasa_neo_project.dto.NeoDto;

import java.time.LocalDate;
import java.util.List;

public interface NeoDao {

    long upsertImportRunStart(LocalDate requestedDate);

    void markImportRunSuccess(long importRunId);

    void markImportRunFailed(long importRunId, String errorMessage);

    void upsertNeo(NeoDto neoDto);

    void batchUpsertCloseApproaches(List<CloseApproachDto> items);

}
