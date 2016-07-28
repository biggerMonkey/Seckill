package com.seckill.dto;

import javax.swing.undo.StateEdit;

import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStatEnum;
/**
 * 封装秒杀执行后结果
 * @author hwj
 *
 */
public class SeckillExecution{
	private long seckillId;
	//状态执行结果
	private int state;
	//状态表示
	private String stateInfo;
	//秒杀成功对象
	private SuccessKilled successKilled;
	
	@Override
	public String toString() {
		
		return "SeckillExecution{"+
				"seckillId="+seckillId+
				",state="+state+'\''+
				",stateInfo="+stateInfo+
				",successKilled="+successKilled+
				"}";
	}
	
	public SeckillExecution(long seckillId,SeckillStatEnum statEnum,SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state=statEnum.getState();
		this.stateInfo=statEnum.getStateInfo();
		this.successKilled = successKilled;
	}

	public SeckillExecution(long seckillId,SeckillStatEnum statEnum){
		this.seckillId = seckillId;
		this.state=statEnum.getState();
		this.stateInfo=statEnum.getStateInfo();
	}
	public SeckillExecution(long seckillId, int state, String stateInfo) {
		super();
		this.seckillId = seckillId;
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public SeckillExecution(long seckillId, int state) {
		super();
		this.seckillId = seckillId;
		this.state = state;
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}
	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}
	
}
