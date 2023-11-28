DROP database if exists warehouse;
create database warehouse;

use warehouse;

-- date dim 
DROP TABLE if exists date_dim;
CREATE TABLE date_dim (
    date_sk INT PRIMARY KEY,
    full_date VARCHAR(500),
    day_since_2020 VARCHAR(500),
    month_since_2020 VARCHAR(500),
    day_of_week VARCHAR(500),
    calendar_month VARCHAR(500),
    calendar_year VARCHAR(500),
    calendar_year_month VARCHAR(500),
    day_of_month VARCHAR(500),
    day_of_year VARCHAR(500),
    week_of_year_sunday VARCHAR(500),
    year_week_sunday VARCHAR(500),
    week_sunday_start VARCHAR(500),
    week_of_year_monday VARCHAR(500),
    year_week_monday VARCHAR(500),
    week_monday_start VARCHAR(500),
    holiday VARCHAR(500),
    day_type VARCHAR(500)
);

TRUNCATE TABLE date_dim;
-- load data ininfile
LOAD DATA INFILE 'D:\\github\\Weather_DataWarehouse\\DataWareHouse\\date_dim_without_quarter.csv' INTO TABLE date_dim FIELDS TERMINATED BY ','
-- OPTIONALLY ENCLOSED BY '"' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
( 
date_sk , 
full_date , 
day_since_2020 , 
month_since_2020 , 
day_of_week , 
calendar_month , 
calendar_year , 
calendar_year_month , 
day_of_month , 
day_of_year , 
week_of_year_sunday , 
year_week_sunday , 
week_sunday_start , 
week_of_year_monday , 
year_week_monday , 
week_monday_start , 
holiday , 
day_type 
); 


-- time dim 
DROP TABLE if exists time_dim;
CREATE TABLE time_dim (
    time_sk INT PRIMARY KEY,
    _hour VARCHAR(2),
		_minute VARCHAR(2),
		_second VARCHAR(2),
		full_time VARCHAR(10)
);

TRUNCATE TABLE time_dim;
LOAD DATA INFILE 'D:\\github\\Weather_DataWarehouse\\DataWareHouse\\time_dim.csv' INTO TABLE time_dim FIELDS TERMINATED BY ','
-- OPTIONALLY ENCLOSED BY '"' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
( 
time_sk , 
_hour,
_minute,
_second,
full_time
); 

-- city
drop table if EXISTS city_dim;
create table city_dim(
	id int primary key auto_increment,
	city_id VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	city_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	city_lat FLOAT NULL default 0,
	city_lon FLOAT null DEFAULT 0,
	city_country VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	city_population INT NULL DEFAULT 0,
	city_timezone INT NULL DEFAULT 0,
	city_sunset INT NULL DEFAULT 0,
	city_sunrise INT NULL DEFAULT 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- main
drop table if exists main_dim;
create table main_dim(
	id int primary key auto_increment,
	main_temp DOUBLE null default 0,
	main_feels_like DOUBLE null default 0,
	main_temp_min DOUBLE null default 0,
	main_temp_max DOUBLE null default 0,
	main_pressure INT null default 0,
	main_sea_level INT null DEFAULT 0,
	main_grnd_level INT null DEFAULT 0,
	main_humidity INT null default 0,
	main_temp_kf DOUBLE null default 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- weather
drop table if exists weather_dim;
create table weather_dim(
	id int primary key auto_increment,
	weather_id INT null default 0,
	weather_main VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	weather_description VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	weather_icon VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- clouds 
drop table if exists clouds_dim;
create table clouds_dim(
	id int primary key auto_increment,
	clouds_all INT null default 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- wind 
drop table if EXISTS wind_dim;
create table wind_dim(
	id int primary key auto_increment,
	wind_speed DOUBLE null default 0,
	wind_deg INT null default 0,
	wind_gust DOUBLE null default 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- visibility 
drop table if exists visibility_dim;
create table visibility_dim(
	id int primary key auto_increment,
	visibility INT null default 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- pop 
drop table if exists pop_dim;
create table pop_dim(
	id int primary key auto_increment,
	pop DOUBLE null default 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- rain 
drop table if exists rain_dim;
create table rain_dim(
	id int primary key auto_increment,
	rain_3h DOUBLE null DEFAULT 0,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- sys 
drop table if exists sys_dim;
create table sys_dim(
	id int primary key auto_increment,
	sys VARCHAR(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	dt_changed date NULL DEFAULT current_timestamp,
  dt_expired date NULL DEFAULT NULL
);

-- fact
drop table if exists fact;
CREATE TABLE fact (
    id_fact INT PRIMARY KEY AUTO_INCREMENT,
    id_city INT,
    id_time INT,
		id_date INT,
    id_main INT,
    id_weather INT,
    id_clouds INT,
    id_wind INT,
    id_visibility INT,
    id_pop INT,
    id_rain INT,
    id_sys INT,
    isDelete bit(1) NULL DEFAULT b'0',
		dtChanged int NULL DEFAULT current_timestamp,
		dtExpired int NULL DEFAULT NULL,
    FOREIGN KEY (id_city) REFERENCES city_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_time) REFERENCES time_dim(time_sk) ON DELETE RESTRICT ON UPDATE RESTRICT,
		FOREIGN KEY (id_date) REFERENCES date_dim(date_sk) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_main) REFERENCES main_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_weather) REFERENCES weather_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_clouds) REFERENCES clouds_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_wind) REFERENCES wind_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_visibility) REFERENCES visibility_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_pop) REFERENCES pop_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_rain) REFERENCES rain_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FOREIGN KEY (id_sys) REFERENCES sys_dim(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);


-- TRIGGER

-- city
drop trigger if exists dt_expired_city;
create trigger dt_expired_city before insert on city_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- main
drop trigger if exists dt_expired_main;
create trigger dt_expired_main before insert on main_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- weather
drop trigger if exists dt_expired_weather;
create trigger dt_expired_weather before insert on weather_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- clouds 
drop trigger if exists dt_expired_clouds;
create trigger dt_expired_clouds before insert on clouds_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- wind
drop trigger if exists dt_expired_wind;
create trigger dt_expired_wind before insert on wind_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- visibility
drop trigger if exists dt_expired_visibility;
create trigger dt_expired_visibility before insert on visibility_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- pop 
drop trigger if exists dt_expired_pop;
create trigger dt_expired_pop before insert on pop_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- rain
drop trigger if exists dt_expired_rain;
create trigger dt_expired_rain before insert on rain_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- sys 
drop trigger if exists dt_expired_sys;
create trigger dt_expired_sys before insert on sys_dim for each row begin set NEW.dt_expired = "9999-12-31";
end;

-- fact
drop trigger if exists dt_expired_fact;
create trigger dt_expired_fact before insert on fact for each row begin set NEW.dtExpired = "9999-12-31";
end;

