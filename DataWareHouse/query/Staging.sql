drop database if exists staging;

create database staging;
use staging;

DROP table if exists staging;
create table staging (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cod VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    message VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    cnt VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_id VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_latitude VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_longitude VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_country_code VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_population VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_timezone VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_sunrise VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    city_sunset VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    dt VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    dt_txt VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_temp VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_feels_like VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_temp_min VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_temp_max VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_pressure VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_sea_level VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_grnd_level VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_humidity VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    main_temp_kf VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    weather_id VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    weather_main VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    weather_description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    weather_icon VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    clouds_all VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    wind_speed VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    wind_deg VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    wind_gust VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    visibility VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    pop VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    rain_3h VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    sys_pod VARCHAR(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    timeGet VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL
);

-- transform

DROP PROCEDURE IF EXISTS TransformData;
DELIMITER //
CREATE PROCEDURE TransformData()
BEGIN
    
END;

-- transform date
DROP PROCEDURE if EXISTS TransformDate;
CREATE PROCEDURE TransformDate()
BEGIN
		ALTER TABLE staging ADD COLUMN _date INT;
    UPDATE staging
    JOIN warehouse.date_dim AS dim ON CAST(staging.dt_txt AS DATE) = dim.full_date
    SET staging._date = dim.date_sk;
END;

-- transform time 
DROP PROCEDURE IF EXISTS TransformTime;
CREATE PROCEDURE TransformTime()
BEGIN
		ALTER TABLE staging ADD COLUMN _time INT;
		UPDATE staging
		JOIN warehouse.time_dim AS dim ON CAST(staging.dt_txt AS TIME) = dim.full_time
		SET staging._time = dim.time_sk;
END;

-- transform city 
drop procedure if exists TransformCity;
create procedure TransformCity()
begin
		create temporary table TempCity(
			city_id VARCHAR(100)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
			city_name VARCHAR(100)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
			city_lat FLOAT,
			city_lon FLOAT,
			city_country_code VARCHAR(100)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
			city_population INT,
			city_timezone INT
		);
		
		INSERT into TempCity
		select distinct city_id, city_name, city_latitude, city_longitude, city_country_code, city_population, city_timezone FROM staging;
		
		INSERT into warehouse.city_dim(city_id, city_name, city_lat, city_lon, city_country, city_population, city_timezone)
		select city_id, city_name, city_lat, city_lon, city_country_code, city_population, city_timezone from TempCity
		where not exists (
			select 1 from warehouse.city_dim
			where warehouse.city_dim.city_id = TempCity.city_id 
			&& warehouse.city_dim.city_name = TempCity.city_name
			&& warehouse.city_dim.city_lat = TempCity.city_lat 
			&& warehouse.city_dim.city_lon = TempCity.city_lon
			&& warehouse.city_dim.city_country = TempCity.city_country_code 
			&& warehouse.city_dim.city_population = TempCity.city_population
			&& warehouse.city_dim.city_timezone = TempCity.city_timezone
		);
		ALTER TABLE staging ADD COLUMN _city INT;
		
		update staging join warehouse.city_dim as dim on 
		staging.city_id = dim.city_id &&
		staging.city_name = dim.city_name
		set staging._city = dim.id;
		
		drop TEMPORARY table TempCity;
end;
call TransformCity();
	



