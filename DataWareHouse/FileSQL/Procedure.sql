use controller;

create procedure today(statuss varchar(500)) 
begin
SELECT paths ,log_status
FROM controller.file_log 
WHERE date_create >= CURDATE() and log_status=statuss;
end ; 



