package com.seckill.entity;

import java.util.Date;

public class SuccessKilled {
	private long seckillId;
	private long userPhone;
	private short state;
	private Date createTime;
	
	//变通，多对一
	private Seckill seckill;
	
	public Seckill getSeckill() {
		return seckill;
	}
	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public long getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
	public short getState() {
		return state;
	}
	public void setState(short state) {
		this.state = state;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SuccessKiled{"+
				"seckillId="+seckillId+
				",userPhone="+userPhone+
				",state="+state+
				",createTime="+createTime+
				"}";
	}
	
}
