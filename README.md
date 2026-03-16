# NASA Near-Earth Object (NEO) Data Project

## Project Overview
This project implements a data collection and analysis pipeline using NASA’s Near-Earth Object Web Service (NeoWs).  
The goal is to retrieve, store, and analyze near-earth object (NEO) data to better understand asteroid activity and potential hazards near Earth.

The system performs **daily data pulls**, with support for **backfilling missed dates**, and stores results in a local MySQL database for analysis.

---

## Core Questions
This project is designed to answer the following questions:

1. **How many potentially hazardous near-earth objects (PHAs) pass near Earth each day within a given date range?**
2. **What are the top N largest objects that had close approaches during that date range?**
3. **Which objects had the smallest miss distance each day or week?**

---

## Data Collection Strategy
- **Daily API pulls** to collect near-earth object data
- **Backfill capability** to recover data for missed or failed collection days
- Data is persisted locally in a **MySQL database** for querying and analysis
- Designed to support reusable date ranges (not hard-coded to a single month)

---

## Data Source
- **NASA Near-Earth Object Web Service (NeoWs)**
- Public API provided by NASA
- Requires an API key for authenticated requests

---

## High-Level Architecture
- **External API:** NASA NeoWs
- **Backend:** Java application
- **Database:** MySQL (local)
- **Data Model Focus:**
  - Near-Earth Objects (asteroids)
  - Close approach events
  - Daily import tracking (for reliability and backfills)

---

## Analysis Capabilities
Once collected, the stored data enables:
- Daily counts of potentially hazardous asteroids
- Ranking asteroids by estimated diameter
- Identifying closest approaches by miss distance
- Aggregations by day and week

---

## Reliability & Backfilling
To ensure data completeness:
- Each daily pull is tracked
- Missed days can be re-requested and inserted later
- Duplicate data is avoided through database constraints and validation logic

---

## Future Enhancements
- Data visualization (charts and graphs)
- Automated scheduling
- Expanded statistical analysis
- Export to CSV for external analysis tools

---

## Notes
This project emphasizes:
- Learning how to work with real-world APIs
- Handling nested JSON responses
- Designing reliable data ingestion workflows
- Applying database design principles to analytical problems
