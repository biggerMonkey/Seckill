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
			-- ROW_COUNT()������һ���޸�����sql��Ӱ������
		SELECT ROW_COUNT() INTO insert_count;
		-- select insert_count;
		-- row_count()���Ϊ0��δ�޸�����  >0 ��ʾ�޸ĵ�����  <0:sql����/δִ���޸�
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
--  �洢����
-- 	1���洢�����Ż��������м������е�ʫ��
--  2����Ҫ���������洢����
--  3���򵥵��߼�������Ӧ�ô洢����
--  4��QPS��һ����ɱ��6000/qps


set @r_result=-3
CALL execute_sckill(1003,12345678912,NOW(),@r_result);
select @r_result