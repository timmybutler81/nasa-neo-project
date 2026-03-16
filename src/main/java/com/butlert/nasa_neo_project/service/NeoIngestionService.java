package com.butlert.nasa_neo_project.service;

import com.butlert.nasa_neo_project.dao.NeoDao;
import com.butlert.nasa_neo_project.dto.CloseApproachDto;
import com.butlert.nasa_neo_project.dto.NeoDto;
import com.butlert.nasa_neo_project.service.params.RunParams;
import com.butlert.nasa_neo_project.service.params.RunParamsResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class NeoIngestionService {

    private final NeoDao neoDao;
    private final NasaNeoApiService nasaNeoApiService;
    private final ObjectMapper objectMapper;
    private final RunParamsResolver runParamsResolver;

    public NeoIngestionService(NeoDao neoDao, NasaNeoApiService nasaNeoApiService, RunParamsResolver runParamsResolver) {
        this.neoDao = neoDao;
        this.nasaNeoApiService = nasaNeoApiService;
        this.runParamsResolver = runParamsResolver;
        this.objectMapper = new ObjectMapper();
    }

    public void runDaily() {
        RunParams params = runParamsResolver.resolveFromEnv();
        runWithParams(params);
    }

    public void runDaily(LocalDate requestedDate) {
        RunParams params = runParamsResolver.resolveForDaily(requestedDate);
        runWithParams(params);
    }

    private void runWithParams(RunParams params) {
        long importRunId = neoDao.upsertImportRunStart(params.startDate());

        try {
            String responseJson = nasaNeoApiService.fetchFeed(
                    params.startDate(),
                    params.endDate()
            );

            ingestNeos(responseJson, importRunId);
            neoDao.markImportRunSuccess(importRunId);

        } catch (Exception e) {
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? e.getClass().getSimpleName()
                    : e.getMessage();

            neoDao.markImportRunFailed(importRunId, msg);
            throw new RuntimeException(e);
        }
    }

    private void ingestNeos(String responseJson, long importRunId) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);

        JsonNode neoByDate = root.path("near_earth_objects");
        if (!neoByDate.isObject()) {
            throw new IllegalStateException("near_earth_objects is missing or not an object");
        }

        int bucketCount = neoByDate.size();
        if (bucketCount == 0) {
            System.out.println("No date buckets returned (near_earth_objects empty).");
            return;
        }

        System.out.println("Date buckets returned: " + bucketCount);

        Iterator<Map.Entry<String, JsonNode>> dateEntries = neoByDate.fields();
        while (dateEntries.hasNext()) {
            Map.Entry<String, JsonNode> entry = dateEntries.next();
            String dateKey = entry.getKey();
            JsonNode neoArray = entry.getValue();

            int neoCount = neoArray.size();
            int phaCount = 0;

            List<CloseApproachDto> approachesToSave = new ArrayList<>();

            for (JsonNode neoNode : neoArray) {
                boolean hazardous = neoNode.path("is_potentially_hazardous_asteroid").asBoolean(false);
                if (hazardous) phaCount++;

                String neoId = neoNode.path("id").asText();
                String name = neoNode.path("name").asText();

                JsonNode km = neoNode.path("estimated_diameter").path("kilometers");
                BigDecimal diaMinKm = km.path("estimated_diameter_min").decimalValue();
                BigDecimal diaMaxKm = km.path("estimated_diameter_max").decimalValue();

                NeoDto dto = new NeoDto(neoId, name, hazardous, diaMinKm, diaMaxKm);
                neoDao.upsertNeo(dto);

                //close approach data
                JsonNode cad = neoNode.path("close_approach_data");
                if (cad.isArray()) {
                    for (JsonNode ca : cad) {
                        long epochMillis = ca.path("epoch_date_close_approach").asLong(0L);
                        if (epochMillis == 0L) continue;

                        LocalDateTime approachUtc = Instant.ofEpochMilli(epochMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDateTime();

                        String orbitingBody = ca.path("orbiting_body").asText("");

                        String missMilesStr = ca.path("miss_distance").path("miles").asText(null);
                        String velMphStr = ca.path("relative_velocity").path("miles_per_hour").asText(null);

                        if (missMilesStr == null || velMphStr == null) continue;

                        BigDecimal missMiles = new BigDecimal(missMilesStr);
                        BigDecimal velMph = new BigDecimal(velMphStr);

                        CloseApproachDto caDto = new CloseApproachDto(
                                neoId,
                                approachUtc,
                                approachUtc.toLocalDate(),
                                orbitingBody,
                                missMiles,
                                velMph,
                                importRunId
                        );

                        approachesToSave.add(caDto);
                    }
                }
            }

            neoDao.batchUpsertCloseApproaches(approachesToSave);

            System.out.println("Date=" + dateKey + " NEOs=" + neoCount + " PHAs=" + phaCount
                    + " CloseApproachesSaved=" + approachesToSave.size());
        }
    }
}