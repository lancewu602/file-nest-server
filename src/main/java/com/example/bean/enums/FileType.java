package com.example.bean.enums;

import lombok.Getter;

/**
 * @author WuQinglong
 * @date 2025/9/3 11:18
 */
@Getter
public enum FileType {

    FILE("file"),
    DIRECTORY("directory"),
    UNKNOWN("unknown"),
    ;

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public static boolean isFile(String value) {
        return FILE.value.equals(value);
    }

    public static boolean isDirectory(String value) {
        return DIRECTORY.value.equals(value);
    }

}
