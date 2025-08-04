package com.dvm.bookstore.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;

@Slf4j
public class DataUtils {

    // Kiểm tra Long
    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    // Kiểm tra Integer
    public static boolean isNullOrZero(Integer value) {
        return (value == null || value.equals(0));
    }

    // Kiểm tra Double
    public static boolean isNullOrZero(Double value) {
        return (value == null || value.equals(0.0));
    }

    // Kiểm tra Float
    public static boolean isNullOrZero(Float value) {
        return (value == null || value.equals(0.0f));
    }

    // Kiểm tra Short
    public static boolean isNullOrZero(Short value) {
        return (value == null || value.equals((short) 0));
    }

    // Kiểm tra Byte
    public static boolean isNullOrZero(Byte value) {
        return (value == null || value.equals((byte) 0));
    }

    // Kiểm tra String
    public static boolean isNullOrEmpty(String value) {
        return StringUtils.isBlank(value);
    }

    // Kiểm tra LocalDate
    public static boolean isNull(LocalDate date) {
        return date == null;
    }

    // Kiểm tra Object
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    // Kiểm tra Boolean
    public static boolean isNullOrFalse(Boolean flag) {
        return flag == null || !flag;
    }

    // Phương thức kiểm tra hợp lệ cho từng kiểu dữ liệu
    public static boolean isValidString(String str) {
        return !isNullOrEmpty(str);
    }

    public static boolean isValidLocalDate(LocalDate date) {
        return date != null;
    }

    public static boolean isValidDouble(Double value) {
        return value != null;
    }

    public static boolean isValidFloat(Float value) {
        return value != null;
    }

    public static boolean isValidInteger(Integer value) {
        return value != null;
    }

    public static boolean isValidLong(Long value) {
        return value != null;
    }

    public static boolean isValidShort(Short value) {
        return value != null;
    }

    public static boolean isValidByte(Byte value) {
        return value != null;
    }

    public static boolean isValidBoolean(Boolean flag) {
        return flag != null;
    }

    public static boolean isValidObject(Object obj) {
        return obj != null;
    }

    public static String objectToJson(Object obj) {
        if(obj == null) {
            return "{}";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // muc dich cho LocalDate
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // bỏ qua các thuộc tính không biết
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // chuyển đổi LocalDate sang định dạng chuỗi
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to convert object to JSON: {}", e.getMessage());
            return "{}";
        }
    }
}
