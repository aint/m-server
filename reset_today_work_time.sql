USE matrix;

SET GLOBAL event_scheduler = 1;
DROP EVENT IF EXISTS reset_today_work_time;

DELIMITER $$
CREATE EVENT reset_today_work_time
  ON SCHEDULE EVERY '1' DAY
  STARTS '2016-11-01 23:59:59'
    DO
    BEGIN
      UPDATE project SET today_minutes = 0;
    END$$
DELIMITER ;