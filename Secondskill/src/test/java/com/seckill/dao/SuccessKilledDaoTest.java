package com.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.entity.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit  spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	
	@Resource
	private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled() {
		long id=1002L;
		long userPhone=12345678999L;
		int insertCount=successKilledDao.insertSuccessKilled(id, userPhone);
		System.out.println(insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long id=1002L;
		long userPhone=12345678999L;
		SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(id, userPhone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}

}
