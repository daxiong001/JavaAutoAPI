package com.yzt.entity;

/**
 * 待分配师傅信息
 * 
 * @author vivi.zhang
 *
 */
public class Worker {
	
	private String realname;
	
	private String id;

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Worker [realname=" + realname + ", id=" + id + "]";
	}
}
