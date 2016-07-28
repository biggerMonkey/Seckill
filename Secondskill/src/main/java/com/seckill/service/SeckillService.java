package com.seckill.service;

import java.util.List;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.excepiton.RepeatKillException;
import com.seckill.excepiton.SeckillCloseException;
import com.seckill.excepiton.SeckillException;

/**
 * 业务接口：站在“使用者”角度设计接口--不繁琐，不抽象
 * 1、方法定义粒度：明确，参数类型，参数个数等，不考了具体实现
 * 2、参数：简练，直接，尽量不直接用Map等
 * 3、返回：类型明确，尽量不返回Map等
 * @author hwj
 *
 */
public interface SeckillService {
	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckilList();
	/**
	 * 查询单个秒杀记录
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	/**
	 * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
	 * @param seckillId
	 * @return
	 */
	Exposer exportSeckillUrl(long seckillId);
	/**
	 * 执行秒杀操作
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
		throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * 执行秒杀操作 by 存储过程
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
