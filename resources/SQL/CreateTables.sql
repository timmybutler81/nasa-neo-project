-- =========================================================
-- NASA NEO Project - MySQL Table Creation
-- Option 1: One import_run row per requested_date (updated)
-- =========================================================

CREATE DATABASE IF NOT EXISTS nasa_neo
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE nasa_neo;

-- -----------------------------
-- 1) import_run
-- -----------------------------
CREATE TABLE IF NOT EXISTS import_run (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  requested_date DATE NOT NULL,
  run_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status ENUM('SUCCESS','FAILED') NOT NULL,
  error_message TEXT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_import_run_requested_date (requested_date)
) ENGINE=InnoDB;

-- -----------------------------
-- 2) neo
-- -----------------------------
CREATE TABLE IF NOT EXISTS neo (
  neo_id VARCHAR(32) NOT NULL,  -- NASA NEO id is numeric-looking but safest as string
  name VARCHAR(255) NOT NULL,
  is_potentially_hazardous BOOLEAN NOT NULL,
  diameter_km_min DECIMAL(12,6) NOT NULL,
  diameter_km_max DECIMAL(12,6) NOT NULL,
  PRIMARY KEY (neo_id),
  KEY idx_neo_hazardous (is_potentially_hazardous)
) ENGINE=InnoDB;

-- -----------------------------
-- 3) close_approach
-- -----------------------------
CREATE TABLE IF NOT EXISTS close_approach (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  neo_id VARCHAR(32) NOT NULL,
  approach_datetime DATETIME NOT NULL,
  approach_date DATE NOT NULL,
  orbiting_body VARCHAR(32) NOT NULL, -- you’ll store 'Earth' only, but keep generic
  miss_distance_miles DECIMAL(16,6) NOT NULL,
  relative_velocity_mph DECIMAL(16,6) NOT NULL,
  import_run_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),

  -- Prevent duplicates across reruns/backfills
  UNIQUE KEY uq_close_approach_event (neo_id, approach_datetime, orbiting_body),

  -- Indexes to support your analysis queries
  KEY idx_close_approach_date (approach_date),
  KEY idx_close_approach_miss_distance (miss_distance_miles),

  CONSTRAINT fk_close_approach_neo
    FOREIGN KEY (neo_id) REFERENCES neo(neo_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

  CONSTRAINT fk_close_approach_import_run
    FOREIGN KEY (import_run_id) REFERENCES import_run(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;