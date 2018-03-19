package com.yzt.entity;

public class SignImage implements Cloneable{

	private String etag;
	
	private String id;
	
	private String name;
	
	private String path;
	
	private String url;

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "SignImage [etag=" + etag + ", id=" + id + ", name=" + name + ", path=" + path + ", url=" + url + "]";
	}

	@Override
	public Object clone() {
		SignImage signImage = null;
		try {
			signImage = (SignImage) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return signImage;
	}	
}
