package com.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStatEnum;
import com.seckill.excepiton.RepeatKillException;
import com.seckill.excepiton.SeckillCloseException;
import com.seckill.excepiton.SeckillException;
import com.seckill.service.SeckillService;

import ch.qos.logback.classic.Logger;

@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	// 获取DAO实例，注入Service依赖
	// @Resource
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private RedisDao redisDao;
	@Autowired
	private SuccessKilledDao successKillDao;

	// md5盐值字符串，用于混淆md5
	private final String slat = "asljglasj2%*^())(*L12lasfl";

	@Override
	public List<Seckill> getSeckilList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		// 通过redis缓存,超时的基础上维护一致性
		// 1:访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			// 2：访问数据库
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			} else {
				// 3:放入redis
				redisDao.putSeckill(seckill);
			}
		}

		Date startTime = seckill.getStartTime();

		Date endTime = seckill.getEndTime();
		// 系统当前时间
		Date nowTime = new Date();
		// logger.info("nowTime={}",nowTime.getTime());
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		// md5 ---转换特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		// logger.info("md5={}",md5);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	/**
	 * 使用注解控制事物方法的优点： 1：开发团队达成一致约定，明确标注事物方法的编程风格
	 * 2：保证事物方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求-->剥离到事物方法外部-->做一个更加上层的方法
	 * 3：不是所有的方法都需要事物，如只有一条修改操作，只读操作不需要事物控制
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// 执行秒杀逻辑:减库存+记录购买行为
		Date nowTime = new Date();
		try {
			// 减库存成功，记录购买行为
			int insertCount = successKillDao.insertSuccessKilled(seckillId, userPhone);
			// 唯一，是否已经参与本产品秒杀
			if (insertCount <= 0) {
				// 重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				// 减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					// 没有更新到记录————DAO rollback
					// 秒杀计算————用户
					throw new SeckillCloseException("seckill is closed");
				} else {
					// 秒杀成功 commit
					SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
				}
			}

		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 所有编译器异常，转换为运行期异常
			throw new SeckillException("seckill inner error:" + e.getMessage());
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
//			logger.info("executeSeckillProcedure");
//			logger.info("getMD5={}",getMD5(seckillId));
			return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
		}
		Date kilTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", kilTime);
		map.put("result", null);
		// 执行存储过程，result被赋值
		try {
			seckillDao.killByProcedure(map);
			// 获取result
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				SuccessKilled sk = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}

	/*
	 * public SeckillExecution executeSeckill(long seckillId, long userPhone,
	 * String md5) throws SeckillException, RepeatKillException,
	 * SeckillCloseException { if (md5 == null ||
	 * !md5.equals(getMD5(seckillId))) { throw new SeckillException(
	 * "seckill data rewrite"); } // 执行秒杀逻辑:减库存+记录购买行为 Date nowTime = new
	 * Date(); try { int updateCount = seckillDao.reduceNumber(seckillId,
	 * nowTime); if (updateCount <= 0) { // 没有更新到记录————DAO // 秒杀计算————用户 throw
	 * new SeckillCloseException("seckill is closed"); } else { // 减库存成功，记录购买行为
	 * int insertCount = successKillDao.insertSuccessKilled(seckillId,
	 * userPhone); // 唯一，是否已经参与本产品秒杀 if (insertCount <= 0) { throw new
	 * RepeatKillException("seckill repeated"); } else { SuccessKilled
	 * successKilled = successKillDao.queryByIdWithSeckill(seckillId,
	 * userPhone); return new SeckillExecution(seckillId,
	 * SeckillStatEnum.SUCCESS, successKilled); } } }catch(SeckillCloseException
	 * e1){ throw e1; }catch(RepeatKillException e2){ throw e2; } catch
	 * (Exception e) { logger.error(e.getMessage(),e); //所有编译器异常，转换为运行期异常 throw
	 * new SeckillException("seckill inner error:"+e.getMessage()); } }
	 */
}
