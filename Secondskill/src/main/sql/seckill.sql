DELIMITER $$

USE `seckill`$$

DROP PROCEDURE IF EXISTS `execute_sckill`$$

CREATE DEFINER=`hwj`@`localhost` PROCEDURE `execute_sckill`(IN v_seckill_id BIGINT,IN v_phone BIGINT,
	IN v_kill_time TIMESTAMP,OUT r_result INT)

BEGIN
		DECLARE insert_count INT DEFAULT 0;
		DECLARE update_count INT DEFAULT 0;

		START TRANSACTION;
		INSERT IGNORE INTO success_killed
			(seckill_id,user_phone,create_time)
			VALUES(v_seckill_id,v_phone,v_kill_time);
			-- ROW_COUNT()返回上一条修改类型sql的影响行数
		SELECT ROW_COUNT() INTO insert_count;
		-- select insert_count;
		-- row_count()结果为0：未修改数据  >0 表示修改的行数  <0:sql错误/未执行修改
		IF (insert_count=0) THEN
			ROLLBACK;
			SET r_result=-1;
		ELSEIF(insert_count<0) THEN
			ROLLBACK;
			SET r_result=-2;
		ELSE
			UPDATE seckill
			SET number=number-1
			WHERE seckill_id=v_seckill_id
				AND end_time >v_kill_time
				AND start_time <v_kill_time
				AND number>0;

			SELECT ROW_COUNT() INTO update_count;
			-- SELECT update_count;
			IF(update_count = 0) THEN
				ROLLBACK;
				SET r_result=0;
			ELSEIF(update_count<0) THEN
				ROLLBACK;
				SET r_result=-2;
			ELSE
				COMMIT;
				SET r_result=1;
			END IF;
		END IF;
	END
$$
DELIMITER ;
--  存储过程
-- 	1、存储过程优化：事物行级锁持有的诗句
--  2、不要过度依赖存储过程
--  3、简单的逻辑，可以应用存储过程
--  4、QPS：一个秒杀单6000/qps


set @r_result=-3
CALL execute_sckill(1003,12345678912,NOW(),@r_result);
select @r_result