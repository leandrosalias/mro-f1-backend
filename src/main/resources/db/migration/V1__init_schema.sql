-- 1. ESCUDERÍAS
CREATE TABLE teams (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL UNIQUE,
                       mro_team_name VARCHAR(100) NOT NULL,
                       logo_url TEXT,
                       color_hex VARCHAR(7) DEFAULT '#FFFFFF',
                       current_budget NUMERIC(10, 2) NOT NULL DEFAULT 50.00,
                       constructors_position INT,
                       has_atr_discount BOOLEAN NOT NULL DEFAULT FALSE,
                       single_car_wildcard BOOLEAN NOT NULL DEFAULT FALSE,
                       wildcard_used_driver_id BIGINT
);

-- 2. PILOTOS (Sin columna 'name', usando directamente 'mro_driver_name')
CREATE TABLE drivers (
                         id BIGSERIAL PRIMARY KEY,
                         team_id BIGINT REFERENCES teams(id) ON DELETE SET NULL,
                         discord_user_id VARCHAR(50) UNIQUE,
                         discord_username VARCHAR(100),
                         mro_driver_name VARCHAR(100) NOT NULL UNIQUE,
                         role VARCHAR(30) NOT NULL DEFAULT 'ROLE_DRIVER',
                         is_rookie BOOLEAN NOT NULL DEFAULT FALSE,
                         base_ballast_kg INT NOT NULL DEFAULT 75,
                         penalty_ballast_kg INT NOT NULL DEFAULT 0,
                         workshop_locked BOOLEAN NOT NULL DEFAULT FALSE
);

-- 3. CATÁLOGO ESTÁTICO DE MEJORAS
CREATE TABLE upgrade_catalog (
                                 id SERIAL PRIMARY KEY,
                                 branch VARCHAR(30) NOT NULL,
                                 level INT NOT NULL CHECK (level BETWEEN 1 AND 3),
                                 weight_reduction_kg INT NOT NULL,
                                 rd_cost NUMERIC(10, 2) NOT NULL,
                                 copy_cost NUMERIC(10, 2) NOT NULL,
                                 UNIQUE (branch, level)
);

-- 4. MEJORAS COMPRADAS POR EQUIPO
CREATE TABLE team_upgrades (
                               id BIGSERIAL PRIMARY KEY,
                               team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
                               upgrade_id INT NOT NULL REFERENCES upgrade_catalog(id),
                               car_1_driver_id BIGINT NOT NULL REFERENCES drivers(id),
                               car_2_driver_id BIGINT REFERENCES drivers(id),
                               rd_purchased_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               copy_purchased_at TIMESTAMPTZ,
                               UNIQUE (team_id, upgrade_id)
);

-- 5. CALENDARIO DE CARRERAS (6 FECHAS)
CREATE TABLE races (
                       id BIGSERIAL PRIMARY KEY,
                       round_number INT NOT NULL UNIQUE CHECK (round_number BETWEEN 1 AND 6),
                       circuit_name VARCHAR(100) NOT NULL,
                       mro_championship_code VARCHAR(20) NOT NULL,
                       mro_race_id VARCHAR(20) NOT NULL,
                       race_datetime TIMESTAMPTZ,
                       manual_market_lock BOOLEAN,
                       is_settled BOOLEAN NOT NULL DEFAULT FALSE
);

-- 6. CONTRATOS DE PATROCINADORES POR FECHA
CREATE TABLE team_race_sponsors (
                                    id BIGSERIAL PRIMARY KEY,
                                    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
                                    race_id BIGINT NOT NULL REFERENCES races(id) ON DELETE CASCADE,
                                    sponsor_type VARCHAR(20) NOT NULL,
                                    target_met BOOLEAN,
                                    reward_paid NUMERIC(10, 2),
                                    signed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    UNIQUE (team_id, race_id)
);

-- 7. RESULTADOS DE CARRERA E INCIDENTES FIA
CREATE TABLE race_results (
                              id BIGSERIAL PRIMARY KEY,
                              race_id BIGINT NOT NULL REFERENCES races(id) ON DELETE CASCADE,
                              driver_id BIGINT NOT NULL REFERENCES drivers(id),
                              finish_position INT,
                              mro_points INT DEFAULT 0,
                              race_status VARCHAR(30) NOT NULL,
                              pole_position BOOLEAN NOT NULL DEFAULT FALSE,
                              fastest_lap BOOLEAN NOT NULL DEFAULT FALSE,
                              driver_of_the_day BOOLEAN NOT NULL DEFAULT FALSE,
                              incident_severity VARCHAR(30) NOT NULL DEFAULT 'NONE',
                              UNIQUE (race_id, driver_id)
);

-- 8. HISTORIAL FINANCIERO AUDITABLE (LEDGER INMUTABLE)
CREATE TABLE ledger_transactions (
                                     id BIGSERIAL PRIMARY KEY,
                                     team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
                                     race_id BIGINT REFERENCES races(id),
                                     transaction_type VARCHAR(40) NOT NULL,
                                     amount NUMERIC(10, 2) NOT NULL,
                                     balance_after NUMERIC(10, 2) NOT NULL,
                                     description TEXT NOT NULL,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- SEED DATA: CATÁLOGO DE LAS 12 MEJORAS
INSERT INTO upgrade_catalog (branch, level, weight_reduction_kg, rd_cost, copy_cost) VALUES
                                                                                         ('CHASSIS', 1, 5, 14.00, 5.00),
                                                                                         ('CHASSIS', 2, 6, 22.00, 8.00),
                                                                                         ('CHASSIS', 3, 7, 34.00, 12.00),
                                                                                         ('AERO', 1, 4, 12.00, 4.00),
                                                                                         ('AERO', 2, 6, 22.00, 8.00),
                                                                                         ('AERO', 3, 8, 38.00, 14.00),
                                                                                         ('POWER_UNIT', 1, 4, 13.00, 5.00),
                                                                                         ('POWER_UNIT', 2, 5, 20.00, 7.00),
                                                                                         ('POWER_UNIT', 3, 7, 36.00, 13.00),
                                                                                         ('SUSPENSION', 1, 3, 9.00, 3.00),
                                                                                         ('SUSPENSION', 2, 4, 16.00, 6.00),
                                                                                         ('SUSPENSION', 3, 5, 25.00, 9.00);