package com.yzt.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yzt.entity.CauseMeta;
/**
 * 上线文环境变量存储类
 * @author vivi.zhang
 *
 */
public class Context {
    //存储单个 sc map
	private Map<String, Object> sc = Maps.newHashMap();
	private List<CauseMeta> causeLists = Lists.newArrayList();
	//存储多个sc map
	private List<Map<String, Object>> scList = Lists.newArrayList();

	public List<Map<String, Object>> getSCList() {
		return scList;
	}

	public Context addSCToList(Map<String, Object> sc) {
		scList.add(sc);
		return this;
	}

	public List<CauseMeta> getCauseLists() {
		return causeLists;
	}

	public Context addCauseMeta(CauseMeta causeMeta) {
		causeLists.add(causeMeta);
		return this;
	}

	public Map<String, Object> getSC() {
		return sc;
	}

	public Context addValue(String key, Object object) {
		sc.put(key, object);
		return this;
	}

	public Object getValue(String key) {
		return sc.get(key);
	}

	public List<String> getSCKeys() {
		List<String> list = Lists.newArrayList();
		for (String key : sc.keySet()) {
			list.add(key);
		}
		return list;
	}

	public List<Object> getSCValues() {
		List<Object> list = Lists.newArrayList();
		for (Object value : sc.values()) {
			list.add(value);
		}
		return list;
	}

	public boolean scHasKey(String key) {
		return sc.containsKey(key);
	}

	public boolean scListHasValue() {
		return scList.size() > 0 ? true : false;
	}
}
