-- ���ݿ��ʼ���ű�
-- �������ݿ�
CREATE DATABASE seckill;
-- ʹ�����ݿ�
USE seckill;
-- ������ɱ����
CREATE TABLE seckill(
	`seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '��Ʒ���id',
	`name` VARCHAR(120) NOT NULL COMMENT '��Ʒ����',
	`number` INT NOT NULL COMMENT '�������',
	`start_time` TIMESTAMP NOT NULL COMMENT '��ɱ��ʼʱ��',
	`end_time` TIMESTAMP NOT NULL COMMENT '��ɱ����ʱ��',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '��ɱ����ʱ��',
	PRIMARY KEY (seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='��ɱ����'
-- ��ʼ������
INSERT INTO seckill(NAME,number,start_time,end_time)
VALUES
	('1000Ԫ��ɱiPhone6',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('500Ԫ��ɱipad2',200,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('300Ԫ��ɱС��4',300,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('200Ԫ��ɱ����note',400,'2015-11-01 00:00:00','2015-11-02 00:00:00')
	
--	��ɱ�ɹ���ϸ��
-- 	�û���¼��֤��ص���Ϣ
CREATE TABLE success_killed(
	`seckill_id` BIGINT NOT NULL COMMENT '��Ʒ���id',
	`user_phone` BIGINT NOT NULL COMMENT '�û��ֻ���',
	`state` TINYINT NOT NULL DEFAULT -1 COMMENT '״̬��ʾ��-1 ��Ч 0���ɹ� 1���Ѹ��� 2���ѷ���',
	`create_time` TIMESTAMP NOT NULL COMMENT '����ʱ��',
	PRIMARY KEY(seckill_id,user_phone),/*��������*/
	KEY idx_create_time(create_time)
)ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT='��ɱ�ɹ���ϸ��'
	
-- �������ݿ����̨
-- mysql -u root -p
	