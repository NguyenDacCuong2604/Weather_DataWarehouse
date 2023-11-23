create database controller;

use controller;

CREATE TABLE config (
    id_config INT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(1000),
    author VARCHAR(500),
    mail VARCHAR(500),
    dateTimeNow varchar(500),
    PathFileError varchar(500),
    pathFileCsv  varchar(500),
    status1  varchar(500),
    status2  varchar(500),
    status3  varchar(500),
    status4  varchar(500),
    status5  varchar(500)
    
);

CREATE TABLE file_log (
    id_file INT PRIMARY KEY AUTO_INCREMENT,
    id_config INT,
    date_create TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    log_status VARCHAR(20),
    author VARCHAR(500),
    paths VARCHAR(500)
);