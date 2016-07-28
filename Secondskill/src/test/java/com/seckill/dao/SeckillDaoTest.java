package com.seckill.dao;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.entity.Seckill;
/**
 * 配置spring和junit整合，junit启动时记载springIOC容器
 * @author hwj
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit  spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
	//注入DAO实现依赖
	@Resource
	private SeckillDao seckillDao;

	@Test
	public void testQueryById() {
		long id=1000;
		Seckill seckill=seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}
	
	@Test
	public void testQueryAll() {
		List<Seckill> seckills=seckillDao.queryAll(0, 100);
		for(Seckill seckill:seckills){
			System.out.println(seckill);
		}
	}
	@Test
	public void testReduceNumber() {
		Date killTime=new Date();
		int updateCount=seckillDao.reduceNumber(1000L,killTime);
		System.out.println(updateCount);
	}
}
