package com.butlert.nasa_neo_project.dto;

import java.math.BigDecimal;

public class NeoDto {
    private String neoId;
    private String name;
    private boolean potentiallyHazardous;
    private BigDecimal diameterKmMin;
    private BigDecimal diameterKmMax;
    private Long runId;

    public NeoDto(String neoId) {
        this.neoId = neoId;
    }

    public NeoDto(String neoId, String name, boolean potentiallyHazardous, BigDecimal diameterKmMin, BigDecimal diameterKmMax) {
        this.neoId = neoId;
        this.name = name;
        this.potentiallyHazardous = potentiallyHazardous;
        this.diameterKmMin = diameterKmMin;
        this.diameterKmMax = diameterKmMax;
    }

    public String getNeoId() {
        return neoId;
    }

    public void setNeoId(String neoId) {
        this.neoId = neoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPotentiallyHazardous() {
        return potentiallyHazardous;
    }

    public void setPotentiallyHazardous(boolean potentiallyHazardous) {
        this.potentiallyHazardous = potentiallyHazardous;
    }

    public BigDecimal getDiameterKmMin() {
        return diameterKmMin;
    }

    public void setDiameterKmMin(BigDecimal diameterKmMin) {
        this.diameterKmMin = diameterKmMin;
    }

    public BigDecimal getDiameterKmMax() {
        return diameterKmMax;
    }

    public void setDiameterKmMax(BigDecimal diameterKmMax) {
        this.diameterKmMax = diameterKmMax;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }
}
