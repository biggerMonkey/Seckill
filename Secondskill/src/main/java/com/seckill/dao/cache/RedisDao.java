package com.seckill.dao.cache;

import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.seckill.entity.Seckill;

import ch.qos.logback.classic.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	private JedisPool jedisPool;

	public RedisDao(String ip, int port) {
		jedisPool = new JedisPool(ip, port);
	}

	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	public Seckill getSeckill(long seckillId) {
		logger.info("RedisDao...getSeckill");
		// redis操作逻辑
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				// 并没有实现内部序列化操作
				// get->byte[]->反序列化->Object(Seckill)
				// 采用自定义序列化
				// protostuff：pojo 有set get方法 普通java对象。。不能是String long
				byte[] bytes = jedis.get(key.getBytes());
				if (bytes != null) {
					Seckill seckill = schema.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					// seckill 被反序列
					return seckill;
				}

			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public String putSeckill(Seckill seckill) {
		logger.info("RedisDao...putSeckill");
		// Object->-序列化>byte[]->redis
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, 
							LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				// 超时缓存，缓存时间为timeout
				int timeout = 60 * 60;
				String result=jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
