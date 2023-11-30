-- Kiểm tra xem database controller đã tồn tại chưa, nếu tồn tại thì xóa
DROP DATABASE IF EXISTS controller;

CREATE DATABASE controller character set utf8;
USE controller;

-- Kiểm tra xem có table config chưa
DROP TABLE IF EXISTS config;
-- create table
CREATE TABLE config(
	id INT PRIMARY KEY AUTO_INCREMENT,
	author VARCHAR(255) NULL DEFAULT NULL,
	email VARCHAR(255) NULL DEFAULT NULL,
	filename VARCHAR(255) NULL DEFAULT 'Test',
	directory_file VARCHAR(510) NULL DEFAULT 'D:\\',
	status_config VARCHAR(255) NULL DEFAULT 'OFF',
	detail_file_path VARCHAR(255) NULL DEFAULT NULL,
	flag bit(1) NULL DEFAULT 0,
	finish_at DATETIME NULL DEFAULT NULL,
	created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
	update_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TRIGGER IF EXISTS update_update_at_trigger;
-- TRIGGER
DELIMITER //

CREATE TRIGGER update_update_at_trigger
BEFORE UPDATE ON config
FOR EACH ROW
BEGIN
    IF NEW.author != OLD.author
        OR NEW.email != OLD.email
        OR NEW.filename != OLD.filename
        OR NEW.directory_file != OLD.directory_file
        OR NEW.flag != OLD.flag
    THEN
        SET NEW.update_at = CURRENT_TIMESTAMP();
    END IF;
END;
//

DROP PROCEDURE IF EXISTS UpdateStatus;
-- update STATUS
DELIMITER //
CREATE PROCEDURE UpdateStatus(
    IN input_id INT,
    IN input_status VARCHAR(255)
)
BEGIN
    UPDATE config
    SET status_config = input_status, finish_at = CURRENT_TIMESTAMP()
    WHERE id = input_id;
END;
//

DELIMITER ;

DROP PROCEDURE IF EXISTS UpdatePathFileDetail;
-- update Path File Detail
DELIMITER //
CREATE PROCEDURE UpdatePathFileDetail(
    IN input_id INT,
    IN input_pathfile VARCHAR(255)
)
BEGIN
    UPDATE config
		SET detail_file_path = input_pathfile
    WHERE id = input_id;
END;
//

DELIMITER ;

DROP PROCEDURE IF EXISTS truncate_staging_table;
-- update Path File Detail
DELIMITER //
CREATE PROCEDURE truncate_staging_table()
BEGIN
    TRUNCATE staging.staging;
END;
//

DELIMITER ;

-- transform
DROP PROCEDURE IF EXISTS TransformData;
CREATE PROCEDURE TransformData()
BEGIN
		-- dim 
    call staging.TransformDate();
		call staging.TransformTime();
		call staging.TransformCity();
		call staging.TransformWeather();
		call staging.TransformCreatedDate();
END;


-- load data to WH
DROP PROCEDURE IF EXISTS LoadDataToWH;
CREATE PROCEDURE `LoadDataToWH`()
BEGIN
		
		INSERT INTO warehouse.fact (
			id_city,
			id_time,
			id_date,
			id_weather,
			city_sunset,
			city_sunrise,
			main_temp,
			main_feels_like,
			main_temp_min,
			main_temp_max,
			main_pressure,
			main_grnd_level,
			main_humidity,
			main_temp_kf,
			clouds_all,
			wind_speed,
			wind_deg,
			wind_gust,
			visibility,
			pop,
			rain_3h,
			sys)
		SELECT staging._city, staging._time, staging._date, staging._weather, cast(city_sunset as int) , cast(city_sunrise as int), cast(main_temp as double), cast(main_feels_like as double), cast(main_temp_min as double), cast(main_temp_max as double), cast(main_pressure as int), cast(main_grnd_level as int), cast(main_humidity as int), cast(main_temp_kf as double), cast(clouds_all as int), cast(wind_speed as double), cast(wind_deg as int), cast(wind_gust as double), cast(visibility as int), cast(pop as double), cast(rain_3h as double), sys_pod
		FROM staging.staging;

END;
id_fact INT PRIMARY KEY AUTO_INCREMENT,
	
		main_pressure INT null default 0,
		main_sea_level INT null DEFAULT 0,
		main_grnd_level INT null DEFAULT 0,
		main_humidity INT null default 0,
		main_temp_kf DOUBLE null default 0,
		clouds_all INT null default 0,
		wind_speed DOUBLE null default 0,
		wind_deg INT null default 0,
		wind_gust DOUBLE null default 0,
		visibility INT null default 0,
		pop DOUBLE null default 0,
		rain_3h DOUBLE null DEFAULT 0,
		sys VARCHAR(1) NULL DEFAULT NULL,

