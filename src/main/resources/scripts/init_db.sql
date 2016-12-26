CREATE DATABASE IF NOT EXISTS `matrix` DEFAULT CHARACTER SET utf8 ;

USE matrix;

SET GLOBAL event_scheduler = 1;

DELIMITER $$
CREATE EVENT IF NOT EXISTS reset_today_work_time
  ON SCHEDULE EVERY '1' DAY
  STARTS '2016-11-01 23:59:59'
    DO
    BEGIN
      UPDATE project SET today_minutes = 0;
    END$$
DELIMITER ;

DELIMITER $$
CREATE EVENT IF NOT EXISTS reset_idle_time_monthly
  ON SCHEDULE EVERY '1' MONTH
  STARTS '2016-11-01 23:59:59'
DO
  BEGIN
    UPDATE project SET idle_minutes = 0;
  END$$
DELIMITER ;
