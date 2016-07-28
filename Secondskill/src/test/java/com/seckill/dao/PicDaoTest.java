package com.seckill.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import com.seckill.entity.Pic;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit  spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class PicDaoTest {
	
	@Resource
	private PicDao picDao;
	@Test
	public void testAddPic() throws IOException {
		File file=new File("E:\\Java\\sunshine.jpg");
		InputStream inputStream=new FileInputStream(file);
		byte[] b=FileCopyUtils.copyToByteArray(inputStream);
		System.out.println("length="+b.length);
		Map<String,Object> map=new HashMap<String,Object>();
		Pic pic=new Pic();
		System.out.println(b.length);
		pic.setPicData(b);
		System.out.println("getGraphicData="+pic.getPicData().length);
		map.put("map", b);
		int result=picDao.addPicByPic(pic);
		//int result=picDao.addPicByMap(map);
		System.out.println("result="+result);
	}
	
	@Test
	public void testQueryByMap() throws Exception{
		Map resultmap=new HashMap();
		resultmap=picDao.queryByMap(6);
		byte[] bs=(byte[]) resultmap.get("pic");
		System.out.println(resultmap.get("id"));
		for(int i=0;i<bs.length;i++){
			System.out.print(bs[i]);
		}
		
	}
	/*12:53:23.569 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@4b6a5fa1] will not be managed by Spring
	12:53:23.609 [main] DEBUG com.seckill.dao.PicDao.addPicByMap - ==>  Preparing: insert into pic(pic) values(?) 
	12:53:23.777 [main] DEBUG com.seckill.dao.PicDao.addPicByMap - ==> Parameters: java.io.ByteArrayInputStream@3d57dd48(ByteArrayInputStream)
	12:53:24.354 [main] DEBUG com.seckill.dao.PicDao.addPicByMap - <==    Updates: 1
	12:53:24.368 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@35b314bd]*/
}
