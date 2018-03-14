package com.yzt.entity;
/**
 * 添加追踪信息的原因
 * @author 13074
 *
 */
public class CauseMeta {
	
	private String label;

	private String id;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Meta [label=" + label + ", id=" + id + "]";
	}
}
