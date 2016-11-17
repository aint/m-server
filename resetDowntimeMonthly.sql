USE matrix2;

SET GLOBAL event_scheduler = 1;

DELIMITER $$
CREATE EVENT resetDowntimeMonthly
  ON SCHEDULE EVERY '1' MONTH
  STARTS '2016-11-01 00:00:00'
    DO
    BEGIN
      UPDATE WorkTime `Downtime` SET `downtimeMinutes` = 0;
    END$$
DELIMITER ;