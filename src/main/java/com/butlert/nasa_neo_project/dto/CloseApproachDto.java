package com.butlert.nasa_neo_project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CloseApproachDto {
    private String neoId;
    private LocalDateTime approachDateTime;
    private LocalDate approachDate;
    private String orbitingBody;
    private BigDecimal missDistanceMiles;
    private BigDecimal relativeVelocityMph;
    private Long importRunId;

    public CloseApproachDto() {

    }

    public CloseApproachDto(
            String neoId,
            LocalDateTime approachDateTime,
            LocalDate approachDate,
            String orbitingBody,
            BigDecimal missDistanceMiles,
            BigDecimal relativeVelocityMph,
            Long importRunId
    ) {
        this.neoId = neoId;
        this.approachDateTime = approachDateTime;
        this.approachDate = approachDate;
        this.orbitingBody = orbitingBody;
        this.missDistanceMiles = missDistanceMiles;
        this.relativeVelocityMph = relativeVelocityMph;
        this.importRunId = importRunId;
    }

    public String getNeoId() {
        return neoId;
    }

    public void setNeoId(String neoId) {
        this.neoId = neoId;
    }

    public LocalDateTime getApproachDateTime() {
        return approachDateTime;
    }

    public void setApproachDateTime(LocalDateTime approachDateTime) {
        this.approachDateTime = approachDateTime;
    }

    public LocalDate getApproachDate() {
        return approachDate;
    }

    public void setApproachDate(LocalDate approachDate) {
        this.approachDate = approachDate;
    }

    public String getOrbitingBody() {
        return orbitingBody;
    }

    public void setOrbitingBody(String orbitingBody) {
        this.orbitingBody = orbitingBody;
    }

    public BigDecimal getMissDistanceMiles() {
        return missDistanceMiles;
    }

    public void setMissDistanceMiles(BigDecimal missDistanceMiles) {
        this.missDistanceMiles = missDistanceMiles;
    }

    public BigDecimal getRelativeVelocityMph() {
        return relativeVelocityMph;
    }

    public void setRelativeVelocityMph(BigDecimal relativeVelocityMph) {
        this.relativeVelocityMph = relativeVelocityMph;
    }

    public Long getImportRunId() {
        return importRunId;
    }

    public void setImportRunId(Long importRunId) {
        this.importRunId = importRunId;
    }
}
