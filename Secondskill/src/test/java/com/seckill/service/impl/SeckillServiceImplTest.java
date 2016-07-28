package com.seckill.service.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.excepiton.RepeatKillException;
import com.seckill.excepiton.SeckillCloseException;
import com.seckill.excepiton.SeckillException;
import com.seckill.service.SeckillService;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.ScanException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
						"classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	@Test
	public void testGetSeckilList() {
		List<Seckill> list=seckillService.getSeckilList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetById() {
		long id=1000;
		Seckill seckill=seckillService.getById(id);
		logger.info("seckill={}",seckill);
	}
	//测试代码完整逻辑，注意可重复执行
	@Test
	public void testSeckillLogic() {
		long id=1001;
		Exposer exposer=seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long phone=12345678888L;
			String md5=exposer.getMd5();
			try{
				SeckillExecution execution=seckillService.executeSeckill(id, phone, md5);
				logger.info("result={}",execution);
			}catch(RepeatKillException e){
				logger.error(e.getMessage());
			}catch(SeckillCloseException e){
				logger.error(e.getMessage());
			}
		}else{//秒杀未开启
			logger.warn("exposer={}",exposer);
		}
	}
	
	@Test
	public void executeSeckillProcedure(){
		long seckillId=1001;
		long phone=12345678902L;
		Exposer exposer=seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			String md5=exposer.getMd5();
			logger.info("md5={}",md5);
			SeckillExecution execution=seckillService.executeSeckillProcedure(seckillId, phone, md5);
			logger.info("executionInfo={}",execution.getStateInfo());
		}
	}
}
