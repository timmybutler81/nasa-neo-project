package com.butlert.nasa_neo_project.service.params;

import java.time.LocalDate;

public record RunParams(String runMode, LocalDate startDate, LocalDate endDate) { }
