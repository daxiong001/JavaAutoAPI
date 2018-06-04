package com.yzt.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yzt.entity.CauseMeta;
import com.yzt.entity.SignImage;
import com.yzt.entity.Worker;

/**
 * 上下文环境变量存储类
 *
 * @param <T>
 * @param <T>
 * @author vivi.zhang
 */
public class Context<T> {
    // 存储单个 sc map，零散值表
    private Map<String, Object> sc = Maps.newHashMap();

    // 存储多个sc map(中间过程数据)
    private List<Map<String, Object>> scs = Lists.newArrayList();

    //实体容器
    private List<T> objects = Lists.newArrayList();

    public List<Map<String, Object>> getScs() {
        return scs;
    }

    public Context addSCToList(Map<String, Object> sc) {
        scs.add(sc);
        return this;
    }

    public List<T> getObjects() {
        return objects;
    }

    public Context addObject(T object) {
        objects.add(object);
        return this;
    }

    public Map<String, Object> getSc() {
        return sc;
    }

    public Context addValue(String key, Object object) {
        sc.put(key, object);
        return this;
    }

    public Object getValue(String key) {
        if (sc.containsKey(key)) {
            return sc.get(key);
        } else {
            return null;
        }
    }

    public List<String> getKeys() {
        List<String> list = Lists.newArrayList();
        for (String key : sc.keySet()) {
            list.add(key);
        }
        return list;
    }

    public List<Object> getValues() {
        List<Object> list = Lists.newArrayList();
        for (Object value : sc.values()) {
            list.add(value);
        }
        return list;
    }

    public boolean hasKey(String key) {
        return sc.containsKey(key);
    }
}
