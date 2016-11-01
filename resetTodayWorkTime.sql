USE matrix2;

SET GLOBAL event_scheduler = 1;

# SHOW PROCESSLIST;

DELIMITER $$
CREATE EVENT resetDailyWorkTime
  ON SCHEDULE EVERY '1' DAY
  STARTS '2016-11-01 10:00:00'
    DO
    BEGIN
      UPDATE `WorkTime` SET `todayMinutes`=0;
    END$$
DELIMITER ;