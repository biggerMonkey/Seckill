package com.seckill.entity;

import java.util.Map;

public class Pic {
	private int id;  
	//private Map<String, Object> imageData;
	private byte[] picData;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	/*public Map<String, Object> getImageData() {
		return imageData;
	}
	public void setImageData(Map<String, Object> imageData) {
		this.imageData = imageData;
	}*/
	public byte[] getPicData() {
		return picData;
	}
	public void setPicData(byte[] picData) {
		this.picData = picData;
	}
}
