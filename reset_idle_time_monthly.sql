USE matrix;

SET GLOBAL event_scheduler = 1;

DROP EVENT IF EXISTS reset_idle_time_monthly;

DELIMITER $$
CREATE EVENT reset_idle_time_monthly
  ON SCHEDULE EVERY '1' MONTH
  STARTS '2016-11-01 23:59:59'
    DO
    BEGIN
      UPDATE project SET idle_minutes = 0;
    END$$
DELIMITER ;