package com.seckill.dao;


import java.util.Map;

import com.seckill.entity.Pic;

public interface PicDao {
	int addPicByPic(Pic pic);
	int addPicByMap(Map< String, Object> map);
	
	Map<String, Object> queryByMap(int id);
	
	Pic queryByPic(int id);
}
