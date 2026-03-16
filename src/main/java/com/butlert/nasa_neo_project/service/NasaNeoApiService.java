package com.butlert.nasa_neo_project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@Service
public class NasaNeoApiService {

    private static final String BASE_PATH = "neo/rest/v1/feed";
    private static final String SCHEME = "https";
    private static final String HOST = "api.nasa.gov";

    private final RestClient restClient;

    public NasaNeoApiService() {
        this.restClient = RestClient.create();
    }

    /**
     * Fetches the NEO feed JSON from NASA for the given date range (inclusive).
     * This method does NOT decide what dates to use; it just executes the HTTP call.
     */
    public String fetchFeed(LocalDate startDate, LocalDate endDate) {
        String apiKey = System.getenv("NASA_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("NASA_API_KEY environment variable is missing");
        }

        URI uri = UriComponentsBuilder.newInstance()
                .scheme(SCHEME)
                .host(HOST)
                .path(BASE_PATH)
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("api_key", apiKey)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }
}