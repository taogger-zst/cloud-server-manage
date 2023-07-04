package com.taogger.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 返回体工具类
 * @author taogger
 * @date 2021/8/5 9:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerJSONResult<T> implements Serializable {

    //定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Integer SUCCESS = 0;

    //响应业务状态
    private Integer code;

    //响应消息
    private String msg;

    //响应中的数据
    private T data;

    @JsonIgnore
    private String ok;      //不使用

    public static <T> ServerJSONResult<T> build(Integer status, String msg, T data) {
        return new ServerJSONResult(status, msg, data);
    }
    public static <T> ServerJSONResult<T> ok(T data) {
        return new ServerJSONResult(data);
    }

    public static <T> ServerJSONResult<T> ok() {
        return new ServerJSONResult(null);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == 0;
    }

    public static <T> ServerJSONResult<T> errorMsg(String msg) {
        return new ServerJSONResult(5001, msg, null);
    }

    public static <T> ServerJSONResult<T> errorDisposeMsg(String msg) {
        return new ServerJSONResult(4301, msg, null);
    }
    public static <T> ServerJSONResult<T> errorMsg(Integer code, String msg) {
        return new ServerJSONResult(code, msg, null);
    }

    public static <T> ServerJSONResult<T> paramsErrorMsg(String msg) {
        return new ServerJSONResult(4001, msg, null);
    }

    public static <T> ServerJSONResult<T> accessErrorMsg(String msg) {
        return new ServerJSONResult(4003, msg, null);
    }


    public ServerJSONResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ServerJSONResult(T data) {
        this.code = 0;
        this.msg = "OK";
        this.data = data;
    }

    /**
     * @param jsonData
     * @param clazz
     * @return
     * @Description: 将json结果集转化为LeeJSONResult对象
     * 需要转换的对象是一个类
     * @author leechenxiang
     * @date 2016年4月22日 下午8:34:58
     */
    public static <T> ServerJSONResult<T> formatToPojo(String jsonData, Class<T> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, ServerJSONResult.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isObject()) {
                obj = MAPPER.readValue(data.traverse(), clazz);
            } else if (data.isTextual()) {
                obj = MAPPER.readValue(data.asText(), clazz);
            }
            return (ServerJSONResult<T>) build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param json
     * @return
     * @Description: 没有object对象的转化
     * @author leechenxiang
     * @date 2016年4月22日 下午8:35:21
     */
    public static ServerJSONResult format(String json) {
        try {
            return MAPPER.readValue(json, ServerJSONResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param jsonData
     * @param clazz
     * @return
     * @Description: Object是集合转化
     * 需要转换的对象是一个list
     * @author leechenxiang
     * @date 2016年4月22日 下午8:35:31
     */
    public static <T> ServerJSONResult<T> formatToList(String jsonData, Class<T> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return (ServerJSONResult<T>) build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }
}
