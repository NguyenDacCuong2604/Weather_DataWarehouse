use controller;

create procedure today(statuss varchar(500)) 
begin
SELECT paths ,log_status
FROM controller.file_log 
WHERE date_create >= CURDATE() and log_status=statuss;
end ; 


CREATE PROCEDURE load_file_to_staging(IN filePath VARCHAR(255))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE fileContent TEXT;
    DECLARE currentLine TEXT;
    DECLARE startPos INT DEFAULT 1;
    DECLARE endPos INT;

    TRUNCATE TABLE staging.staging; -- Xóa dữ liệu cũ trong bảng staging

    SET fileContent = LOAD_FILE(filePath);

    -- Kiểm tra nếu file không tồn tại hoặc không đọc được
    IF fileContent IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'File không tồn tại hoặc không thể đọc được.';
    END IF;

    WHILE startPos <= LENGTH(fileContent) DO
        SET endPos = LOCATE('\n', fileContent, startPos); -- Tìm vị trí kết thúc của dòng

        IF endPos = 0 THEN
            SET currentLine = SUBSTRING(fileContent, startPos); -- Nếu không tìm thấy, lấy từ startPos đến cuối chuỗi
        ELSE
            SET currentLine = SUBSTRING(fileContent, startPos, endPos - startPos); -- Lấy dòng từ startPos đến endPos
            SET startPos = endPos + 1; -- Di chuyển startPos đến vị trí sau '\n'
        END IF;

        INSERT INTO staging.staging (
            cod, message, cnt, city_id, city_name, city_latitude, city_longitude, city_country_code, city_population,
            city_timezone, city_sunrise, city_sunset, dt, dt_txt, main_temp, main_feels_like, main_temp_min,
            main_temp_max, main_pressure, main_sea_level, main_grnd_level, main_humidity, main_temp_kf, weather_id,
            weather_main, weather_description, weather_icon, clouds_all, wind_speed, wind_deg, wind_gust, visibility,
            pop, rain_3h, sys_pod, timeGet
        )
        VALUES (
						SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 0), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 1), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 2), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 3), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 4), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 5), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 6), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 7), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 8), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 9), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 10), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 11), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 12), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 13), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 14), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 15), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 16), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 17), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 18), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 19), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 20), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 21), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 22), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 23), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 24), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 25), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 26), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 27), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 28), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 29), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 30), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 31), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 32), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 33), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 34), ',', -1),
            SUBSTRING_INDEX(SUBSTRING_INDEX(currentLine, ',', 35), ',', -1)
        );

        SET startPos = endPos + 1;
    END WHILE;
END 


