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
    created_date VARCHAR(100) NULL DEFAULT NULL
);

-- transform date
DROP PROCEDURE if EXISTS TransformDate;
CREATE PROCEDURE TransformDate()
BEGIN
		ALTER TABLE staging ADD COLUMN if not exists _date INT;
    UPDATE staging
    JOIN warehouse.date_dim AS dim ON CAST(staging.dt_txt AS DATE) = dim.full_date
    SET staging._date = dim.date_sk;
END;

-- transform time 
DROP PROCEDURE IF EXISTS TransformTime;
CREATE PROCEDURE TransformTime()
BEGIN
		ALTER TABLE staging ADD COLUMN if not exists _time INT;
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
		select distinct city_id, city_name, CAST(city_latitude as DOUBLE), CAST(city_longitude as DOUBLE), city_country_code, CAST(city_population as INT), CAST(city_timezone as INT) FROM staging;
		
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
		ALTER TABLE staging ADD COLUMN if not exists _city INT;
		
		update staging join warehouse.city_dim as dim on 
		staging.city_id = dim.city_id &&
		staging.city_name = dim.city_name &&
		cast(staging.city_latitude as FLOAT) = dim.city_lat &&
		cast(staging.city_longitude as FLOAT) = dim.city_lon &&
		staging.city_country_code = dim.city_country &&
		cast(staging.city_population as INT) = dim.city_population &&
		cast(staging.city_timezone as INT) = dim.city_timezone
		set staging._city = dim.id;
		
		drop TEMPORARY table TempCity;
end;

-- transform sun 
drop procedure if exists TransformSun;
create procedure TransformSun()
begin
		create temporary table TempSun(
			city_sunrise INT,
			city_sunset INT
		);
		INSERT into TempSun
		select distinct CAST(city_sunrise as INT), CAST(city_sunset as INT) FROM staging;
		INSERT into warehouse.sun_dim(city_sunrise, city_sunset)
		select city_sunrise, city_sunset from TempSun
		where not exists (
			select 1 from warehouse.sun_dim
			where warehouse.sun_dim.city_sunrise = TempSun.city_sunrise 
			&& warehouse.sun_dim.city_sunset = TempSun.city_sunset
		);
		ALTER TABLE staging ADD COLUMN if not exists _sun INT;
	
		update staging join warehouse.sun_dim as dim on 
		cast(staging.city_sunrise as INT) = dim.city_sunrise &&
		cast(staging.city_sunset as INT) = dim.city_sunset
		set staging._sun = dim.id;		
		drop TEMPORARY table TempSun;
end;

-- transform main 
drop procedure if exists TransformMain;
create procedure TransformMain()
begin
		create temporary table TempMain(
			main_temp DOUBLE,
			main_feels_like DOUBLE,
			main_temp_min DOUBLE,
			main_temp_max DOUBLE,
			main_pressure INT,
			main_sea_level INT,
			main_grnd_level INT,
			main_humidity INT,
			main_temp_kf DOUBLE
		);
		INSERT into TempMain
		select distinct cast(main_temp as Double), cast(main_feels_like as double),
			cast(main_temp_min as double), cast(main_temp_max as double), cast(main_pressure as int),
			cast(main_sea_level as int), cast(main_grnd_level as int), cast(main_humidity as int), cast(main_temp_kf as double)
		 FROM staging;
		INSERT into warehouse.main_dim(main_temp, main_feels_like, main_temp_min, main_temp_max, main_pressure, main_sea_level, main_grnd_level, main_humidity, main_temp_kf)
		select main_temp, main_feels_like, main_temp_min, main_temp_max, main_pressure, main_sea_level, main_grnd_level, main_humidity, main_temp_kf from TempMain
		where not exists (
			select 1 from warehouse.main_dim as dim
			where dim.main_temp = TempMain.main_temp &&
						dim.main_feels_like = TempMain.main_feels_like &&
						dim.main_temp_min = TempMain.main_temp_min &&
						dim.main_temp_max = TempMain.main_temp_max &&
						dim.main_pressure = TempMain.main_pressure &&
						dim.main_sea_level = TempMain.main_sea_level &&
						dim.main_grnd_level = TempMain.main_grnd_level &&
						dim.main_humidity = TempMain.main_humidity &&
						dim.main_temp_kf = TempMain.main_temp_kf
		);
		ALTER TABLE staging ADD COLUMN if not exists _main INT;
	
		update staging join warehouse.main_dim as dim on 
			cast(staging.main_temp as Double) = dim.main_temp &&
			cast(staging.main_feels_like as double) = dim.main_feels_like &&
			cast(staging.main_temp_min as double) = dim.main_temp_min &&
			cast(staging.main_temp_max as double) = dim.main_temp_max &&
			cast(staging.main_pressure as int) = dim.main_pressure &&
			cast(staging.main_sea_level as int) = dim.main_sea_level &&
			cast(staging.main_grnd_level as int) = dim.main_grnd_level &&
			cast(staging.main_humidity as int) = dim.main_humidity &&
			cast(staging.main_temp_kf as double) = dim.main_temp_kf
		set staging._main = dim.id;		
		drop TEMPORARY table TempMain;
end;

-- transform weather
drop procedure if exists TransformWeather;
create procedure TransformWeather()
begin
		create temporary table TempWeather(
			weather_id INT,
			weather_main VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
			weather_description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
			weather_icon VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
		);
		INSERT into TempWeather
		select distinct cast(weather_id as int), weather_main, weather_description, weather_icon
		 FROM staging;
		INSERT into warehouse.weather_dim(weather_id, weather_main, weather_description, weather_icon)
		select weather_id, weather_main, weather_description, weather_icon from TempWeather
		where not exists (
			select 1 from warehouse.weather_dim as dim
			where dim.weather_id = TempWeather.weather_id &&
						dim.weather_main = TempWeather.weather_main &&
						dim.weather_description = TempWeather.weather_description &&
						dim.weather_icon = TempWeather.weather_icon
						
		);
		ALTER TABLE staging ADD COLUMN if not exists _weather INT;
	
		update staging join warehouse.weather_dim as dim on 
			cast(staging.weather_id as INT) = dim.weather_id &&
			staging.weather_main = dim.weather_main &&
			staging.weather_description = dim.weather_description &&
			staging.weather_icon = dim.weather_icon
		set staging._weather = dim.id;		
		drop TEMPORARY table TempWeather;
end;

-- transform clouds 
drop procedure if exists TransformClouds;
create procedure TransformClouds()
begin
		create temporary table TempClouds(
			clouds_all INT
		);
		INSERT into TempClouds
		select distinct cast(clouds_all as int) FROM staging;
		INSERT into warehouse.clouds_dim(clouds_all)
		select clouds_all from TempClouds
		where not exists (
			select 1 from warehouse.clouds_dim as dim
			where dim.clouds_all = TempClouds.clouds_all
		);
		ALTER TABLE staging ADD COLUMN if not exists _clouds INT;
	
		update staging join warehouse.clouds_dim as dim on 
			cast(staging.clouds_all as INT) = dim.clouds_all
		
		set staging._clouds = dim.id;		
		drop TEMPORARY table TempClouds;
end;

-- transform wind
drop procedure if exists TransformWind;
create procedure TransformWind()
begin
		create temporary table TempWind(
			wind_speed double,
			wind_deg int,
			wind_gust double
		);
		INSERT into TempWind
		select distinct cast(wind_speed as double), cast(wind_deg as int), cast(wind_gust as int) FROM staging;
		INSERT into warehouse.wind_dim(wind_speed, wind_deg, wind_gust)
		select wind_speed, wind_deg, wind_gust from TempWind
		where not exists (
			select 1 from warehouse.wind_dim as dim
			where dim.wind_speed = TempWind.wind_speed &&
						dim.wind_deg = TempWind.wind_deg &&
						dim.wind_gust = TempWind.wind_gust
		);
		ALTER TABLE staging ADD COLUMN if not exists _wind INT;
	
		update staging join warehouse.wind_dim as dim on 
			cast(staging.wind_speed as double) = dim.wind_speed &&
			cast(staging.wind_deg as int) = dim.wind_deg &&
			cast(staging.wind_gust as int) = dim.wind_gust
	
		set staging._wind = dim.id;		
		drop TEMPORARY table TempWind;
end;

-- transform visibility
drop procedure if exists TransformVisibility;
create procedure TransformVisibility()
begin
		create temporary table TempVisibility(
			visibility int
		);
		INSERT into TempVisibility
		select distinct cast(visibility as int) FROM staging;
		INSERT into warehouse.visibility_dim(visibility)
		select visibility from TempVisibility
		where not exists (
			select 1 from warehouse.visibility_dim as dim
			where dim.visibility = TempVisibility.visibility
		);
		ALTER TABLE staging ADD COLUMN if not exists _visibility INT;
	
		update staging join warehouse.visibility_dim as dim on 
			cast(staging.visibility as int) = dim.visibility	
		set staging._visibility = dim.id;		
		drop TEMPORARY table TempVisibility;
end;

-- transform pop 
drop procedure if exists TransformPop;
create procedure TransformPop()
begin
		create temporary table TempPop(
			pop double 
		);
		INSERT into TempPop
		select distinct cast(pop as double) FROM staging;
		INSERT into warehouse.pop_dim(pop)
		select pop from TempPop
		where not exists (
			select 1 from warehouse.pop_dim as dim
			where dim.pop = TempPop.pop
		);
		ALTER TABLE staging ADD COLUMN if not exists _pop INT;
	
		update staging join warehouse.pop_dim as dim on 
			cast(staging.pop as double) = dim.pop	
		set staging._pop = dim.id;		
		drop TEMPORARY table TempPop;
end;

-- transform rain 
drop procedure if exists TransformRain;
create procedure TransformRain()
begin
		create temporary table TempRain(
			rain_3h double 
		);
		INSERT into TempRain
		select distinct cast(rain_3h as double) FROM staging;
		INSERT into warehouse.rain_dim(rain_3h)
		select rain_3h from TempRain
		where not exists (
			select 1 from warehouse.rain_dim as dim
			where dim.rain_3h = TempRain.rain_3h
		);
		ALTER TABLE staging ADD COLUMN if not exists _rain INT;
	
		update staging join warehouse.rain_dim as dim on 
			cast(staging.rain_3h as double) = dim.rain_3h
		set staging._rain = dim.id;		
		drop TEMPORARY table TempRain;
end;

-- transform sys 
drop procedure if exists TransformSys;
create procedure TransformSys()
begin
		create temporary table TempSys(
			sys VARCHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci 
		);
		INSERT into TempSys
		select distinct sys_pod FROM staging;
		INSERT into warehouse.sys_dim(sys)
		select sys from TempSys
		where not exists (
			select 1 from warehouse.sys_dim as dim
			where dim.sys = TempSys.sys
		);
		ALTER TABLE staging ADD COLUMN if not exists _sys INT;
	
		update staging join warehouse.sys_dim as dim on 
			staging.sys_pod = dim.sys
		set staging._sys = dim.id;		
		drop TEMPORARY table TempSys;
end;

-- transform created date 
drop procedure if exists TransformCreatedDate;
create procedure TransformCreatedDate()
begin
	UPDATE staging
    JOIN warehouse.date_dim AS dim ON staging.created_date = dim.full_date
    SET staging.created_date = dim.date_sk;
end;



	



