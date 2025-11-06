package com.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Jackson JSON 工具类，封装序列化、反序列化等操作
 * @author WuQinglong
 * @date 2025/10/30 17:17
 */
public class JacksonUtil {

    // 单例 ObjectMapper（线程安全，建议全局复用）
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 配置：忽略未知属性（反序列化时遇到未定义的字段不报错）
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置：日期类型序列化时使用 ISO 格式（如 LocalDateTime 转为标准字符串）
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 可选：格式化输出（生产环境建议关闭，节省空间）
        // OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 将 Java 对象序列化为 JSON 字符串
     * @param obj 任意 Java 对象（实体类、集合、Map 等）
     * @return JSON 字符串，若 obj 为 null 则返回 "null"
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的 Java 对象
     * @param json JSON 字符串
     * @param clazz 目标类型的 Class（如 User.class）
     * @return 反序列化后的对象，若 json 为 null/空则返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败（目标类型：" + clazz.getName() + "）：" + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为泛型类型（如 List<User>、Map<String, User> 等）
     * @param json JSON 字符串
     * @param typeReference 泛型类型引用（如 new TypeReference<List<User>>() {}）
     * @return 反序列化后的泛型对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 泛型反序列化失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将输入流（如文件流、网络流）中的 JSON 反序列化为指定类型对象
     * @param inputStream 输入流
     * @param clazz 目标类型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException("从输入流反序列化 JSON 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串转换为 List<T>（简化泛型调用）
     * @param json JSON 数组字符串（如 "[{...}, {...}]"）
     * @param clazz 集合元素类型（如 User.class）
     * @return List<T> 集合
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        // 利用 TypeReference 处理 List<T> 泛型
        TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
        };
        return fromJson(json, typeReference);
    }

    /**
     * 将 JSON 字符串转换为 Map<String, Object>
     * @param json JSON 对象字符串（如 "{"name":"张三", "age":20}"）
     * @return Map<String, Object>
     */
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 将 JSON 字符串转换为 Map<String, T>（值为指定类型）
     * @param json JSON 对象字符串
     * @param valueType 值的类型（如 User.class）
     * @return Map<String, T>
     */
    public static <T> Map<String, T> toMap(String json, Class<T> valueType) {
        TypeReference<Map<String, T>> typeReference = new TypeReference<Map<String, T>>() {
        };
        return fromJson(json, typeReference);
    }

    /**
     * 禁止实例化（工具类）
     */
    private JacksonUtil() {
        throw new UnsupportedOperationException("工具类不能实例化");
    }
}