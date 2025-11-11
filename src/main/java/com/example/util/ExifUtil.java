package com.example.util;

import com.example.bean.enums.ExifFieldMapping;
import com.example.bean.model.ExifInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WuQinglong
 * @date 2025/10/31 10:05
 */
public class ExifUtil {

    private static final Logger log = LoggerFactory.getLogger(ExifUtil.class);

    /**
     * 从图片中提取Exif信息
     * @param imagePath 图片路径
     * @return 包含Exif信息的ExifInfo对象
     * @throws IOException 如果图片不存在或无法读取
     */
    public static ExifInfo extract(Path imagePath) {
        try {
            String exifContent = CommandUtil.getExif(imagePath);
            return convert(exifContent);
        } catch (IOException e) {
            log.error("无法获取图片的EXIF信息", e);
        }
        return null;
    }

    /**
     * 将Exif信息字符串转换为ExifInfo对象
     * @param exifString 包含Exif信息的字符串，每行一条信息，格式为"key : value"
     * @return 转换后的ExifInfo对象
     */
    public static ExifInfo convert(String exifString) {
        if (exifString == null || exifString.isEmpty()) {
            return null;
        }

        ExifInfo exifInfo = new ExifInfo();
        Map<String, String> exifMap = parseExifStringToMap(exifString);

        // 使用反射设置字段值
        Field[] fields = ExifInfo.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // 使用枚举获取对应的原始key
                String originalKey = ExifFieldMapping.getExifKeyByFieldName(field.getName());
                String value = exifMap.get(originalKey);

                if (value != null) {
                    field.setAccessible(true);
                    field.set(exifInfo, value.trim());
                }
            } catch (Exception e) {
                // 忽略设置字段时的异常，继续处理其他字段
                log.error("设置字段值失败: {}, 原因: {}", field.getName(), e.getMessage());
            }
        }

        return exifInfo;
    }

    /**
     * 将Exif信息字符串解析为键值对映射
     * @param exifString 包含Exif信息的字符串
     * @return 解析后的键值对映射
     */
    private static Map<String, String> parseExifStringToMap(String exifString) {
        Map<String, String> exifMap = new HashMap<>();
        String[] lines = exifString.split("\n");

        for (String line : lines) {
            // 查找第一个":"的位置，分割键和值
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                exifMap.put(key, value);
            }
        }

        return exifMap;
    }
}
