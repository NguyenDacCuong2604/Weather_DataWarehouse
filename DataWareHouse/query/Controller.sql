-- Kiểm tra xem database controller đã tồn tại chưa, nếu tồn tại thì xóa
DROP DATABASE IF EXISTS controller;

CREATE DATABASE controller;
USE controller;

-- Kiểm tra xem có table config chưa
DROP TABLE IF EXISTS config;
-- create table
CREATE TABLE config(
	id INT PRIMARY KEY AUTO_INCREMENT,
	author VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	email VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
	filename VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'Test',
	directory_file VARCHAR(510) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'D:\\',
	status_config VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'OFF',
	detail_file_path VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
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


