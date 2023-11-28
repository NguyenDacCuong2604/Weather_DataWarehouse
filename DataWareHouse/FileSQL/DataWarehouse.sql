create table data_warehouse;

use data_warehouse;

create table dim_city(
	id int primary key auto_increment,
	city_id VARCHAR(100),
	city_name VARCHAR(100),
	city_lat FLOAT,
	city_lon FLOAT,
	city_country VARCHAR(100),
	city_population INT,
	city_timezone INT,
	city_sunset INT,
	city_sunrise INT
);

create table dim_time(
	id int primary key auto_increment,
	dt int,
	dt_txt DATE
);

create table dim_main(
	id int primary key auto_increment,
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

create table dim_weather(
	id int primary key auto_increment,
	weather_id INT,
	weather_main VARCHAR(100),
	weather_description VARCHAR(100),
	weather_icon VARCHAR(100)
);

create table dim_clouds(
	id int primary key auto_increment,
	clouds_all INT
);

create table dim_wind(
	id int primary key auto_increment,
	wind_speed DOUBLE,
	wind_deg INT,
	wind_gust DOUBLE
);

create table dim_visibility(
	id int primary key auto_increment,
	visibility INT
);

create table dim_pop(
	id int primary key auto_increment,
	pop DOUBLE
);

create table dim_rain(
	id int primary key auto_increment,
	rain_3h DOUBLE
);

create table dim_sys(
	id int primary key auto_increment,
	sys VARCHAR(1)
);

CREATE TABLE fact (
    id_fact INT PRIMARY KEY AUTO_INCREMENT,
    id_city INT,
    id_time INT,
    id_main INT,
    id_weather INT,
    id_clouds INT,
    id_wind INT,
    id_visibility INT,
    id_pop INT,
    id_rain INT,
    id_sys INT,
    data_create DATE,
    FOREIGN KEY (id_city) REFERENCES dim_city(id),
    FOREIGN KEY (id_time) REFERENCES dim_time(id),
    FOREIGN KEY (id_main) REFERENCES dim_main(id),
    FOREIGN KEY (id_weather) REFERENCES dim_weather(id),
    FOREIGN KEY (id_clouds) REFERENCES dim_clouds(id),
    FOREIGN KEY (id_wind) REFERENCES dim_wind(id),
    FOREIGN KEY (id_visibility) REFERENCES dim_visibility(id),
    FOREIGN KEY (id_pop) REFERENCES dim_pop(id),
    FOREIGN KEY (id_rain) REFERENCES dim_rain(id),
    FOREIGN KEY (id_sys) REFERENCES dim_sys(id)
);



