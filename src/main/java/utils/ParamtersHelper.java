package utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

/**
 * 参数化工具
 *
 * @author vivi.zhang
 */
public class ParamtersHelper {

    private static Logger logger = Logger.getLogger(ParamtersHelper.class);

    public static ParamtersHelper getInstance() {
        return new ParamtersHelper();
    }

    /**
     * 更新入参
     *
     * @param jsonObject    待更新的json对象
     * @param changedObject 目标更新的对象，更新对象中所以属性值
     * @return
     */
    public <V, T> T updateInputParam(T jsonObject, V changedObject) {
        return (T) analysisJsonAndUpdate(jsonObject, changedObject);
    }

    /**
     * 更新入参
     *
     * @param jsonObject   待更新的json对象
     * @param changedValue 目标更新的键值对，更新所有键值对
     * @return
     */
    public <T> T updateInputParam(T jsonObject, Map<String, Object> changedValue) {
        return (T) analysisJsonAndUpdate(jsonObject, changedValue);
    }

    /**
     * @param inputJson 待更新的json字符串
     * @param key       目标更新的键
     * @param value     目标更新的值
     * @return
     */
    public Object updateInputParam(String inputJson, String key, Object value) {
        return analysisJsonAndUpdate(inputJson, key, value);
    }

    /**
     * 根据类对象，从json字符串中获取类对象属性值，并存储至容器中
     *
     * @param inputJson json字符串
     * @param clazz     实体类对象
     */
    public ParamtersHelper saveParams(String inputJson, Class clazz) {
        ContextUtil.insertParams2Context(inputJson, clazz);
        return this;
    }

    /**
     * 根据类对象，从json字符串中获取类对象属性值，并存储至容器中,
     * list的每个值亦会做为键值对存储在容器中，key = list.get(i);
     *
     * @param inputJson json字符串
     * @param clazz     实体类对象
     * @param list      实体类对象对应属性值
     */
    public ParamtersHelper saveParams(String inputJson, Class clazz, List<String> list) {
        ContextUtil.insertParams2Context(inputJson, clazz, list);
        return this;
    }

    /**
     * 存储多个键值对至容器，
     * list的每个值亦会做为键值对存储在容器中，key = list.get(i);
     *
     * @param inputJson json字符串
     * @param list      待获取的多个jsonkey
     */
    public ParamtersHelper saveParams(String inputJson, List<String> list) {
        ContextUtil.insertParams2Context(inputJson, list);
        return this;
    }

    /**
     * 从json字符串中获取单个键值对，直接存储键值对至容器
     *
     * @param inputJson json 字符串
     * @param sourceKey 源待获取的json key
     * @param targetKey 存储到context map sc容器中的key 对应sourceKey的value值
     */
    public ParamtersHelper saveParams(String inputJson, String sourceKey, String targetKey) {
        ContextUtil.insertParam2Context(inputJson, sourceKey, targetKey);
        return this;
    }


    /**
     * 从json字符串中获取单个键值对，直接存储键值对至容器
     *
     * @param inputJson json 字符串
     * @param key       源待获取的json key
     */
    public ParamtersHelper saveParams(String inputJson, String key) {
        ContextUtil.insertParams2Context(inputJson, key);
        return this;
    }

    /**
     * 直接存储键值对至容器
     */
    public ParamtersHelper saveParam(String key, String value) {
        ContextUtil.insertParam2Context(key, value);
        return this;
    }

    /**
     * 从容器中获取参数clazz对象列表
     *
     * @param clazz
     * @return
     */
    public <T> List<T> readParams(Class<T> clazz) {
        return (List<T>) ContextUtil.getParams(clazz);
    }

    /**
     * 从容器中获取参数Map对象
     */
    public Map<String, Object> readParams() {
        return ContextUtil.getParams();
    }

    /**
     * 从容器中获取参数key,value值
     */
    public Object readParams(String key) {
        return ContextUtil.getParams(key);
    }


    /**
     * 更新json string中指定的多个key值
     *
     * @param jsonObject   json对象
     * @param changedValue 待更新的key和value键值对
     * @return
     */
    private Object analysisJsonAndUpdate(Object jsonObject, Map<String, Object> changedValue) {
        if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObject;
            JSONArray jsonAr = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonAr.add(analysisJsonAndUpdate(jsonArray.get(i), changedValue));
            }
            return jsonAr;
        } else if (jsonObject instanceof JSONObject) {
            JSONObject jsonObt = (JSONObject) jsonObject;
            JSONObject jsonOb = new JSONObject();
            for (String jsonKey : jsonObt.keySet()) {
                if (changedValue.containsKey(jsonKey)) {
                    jsonOb.put(jsonKey, changedValue.get(jsonKey));
                } else if (jsonObt.get(jsonKey) instanceof JSONObject
                        || jsonObt.get(jsonKey) instanceof JSONArray) {
                    jsonOb.put(jsonKey, analysisJsonAndUpdate(jsonObt.get(jsonKey), changedValue));
                } else {
                    jsonOb.put(jsonKey, jsonObt.get(jsonKey));
                }
            }
            return jsonOb;
        } else {
            return jsonObject;
        }
    }

    /**
     * 更新json string中指定的多个key值
     *
     * @param jsonObject    json对象
     * @param changedObject 待更新的对象
     * @return
     */
    private Object analysisJsonAndUpdate(Object jsonObject, Object changedObject) {
        Field[] fds = changedObject.getClass().getDeclaredFields();
        List<String> fieldlist = Lists.newArrayList();
        for (Field field : fds) {
            fieldlist.add(field.getName());
        }
        if (jsonObject instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonObject;
            JSONArray jsonAr = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonAr.add(analysisJsonAndUpdate(jsonArray.get(i), changedObject));
            }
            return jsonAr;
        } else if (jsonObject instanceof JSONObject) {
            JSONObject jsonObt = (JSONObject) jsonObject;
            JSONObject jsonOb = new JSONObject();
            for (String jsonKey : jsonObt.keySet()) {
                if (fieldlist.contains(jsonKey)) {
                    Field field;
                    try {
                        field = changedObject.getClass().getDeclaredField(jsonKey);
                        field.setAccessible(true);
                        jsonOb.put(jsonKey, field.get(changedObject));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (jsonObt.get(jsonKey) instanceof JSONObject
                        || jsonObt.get(jsonKey) instanceof JSONArray) {
                    jsonOb.put(jsonKey, analysisJsonAndUpdate(jsonObt.get(jsonKey), changedObject));
                } else {
                    jsonOb.put(jsonKey, jsonObt.get(jsonKey));
                }
            }
            return jsonOb;
        } else {
            return jsonObject;
        }
    }

    /**
     * 更新json string中指定的单个key和value值
     *
     * @param inputJson    json字符串
     * @param changedValue
     * @return
     */
    private Object analysisJsonAndUpdate(String inputJson, String key, Object value) {
        Object object = JSON.parse(inputJson);
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            JSONArray jsonAr = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonAr.add(analysisJsonAndUpdate(jsonArray.get(i).toString(), key, value));
            }
            return jsonAr;
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            JSONObject jsonOb = new JSONObject();
            for (String jsonKey : jsonObject.keySet()) {
                if (key.equals(jsonKey)) {
                    jsonOb.put(jsonKey, value);
                } else if (jsonObject.get(jsonKey) instanceof JSONObject
                        || jsonObject.get(jsonKey) instanceof JSONArray) {
                    jsonOb.put(jsonKey, analysisJsonAndUpdate(jsonObject.get(jsonKey).toString(), key, value));
                } else {
                    jsonOb.put(jsonKey, jsonObject.get(jsonKey));
                }
            }
            return jsonOb;
        } else {
            return object;
        }
    }

}
