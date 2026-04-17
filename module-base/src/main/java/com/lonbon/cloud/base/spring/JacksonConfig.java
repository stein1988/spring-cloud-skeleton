package com.lonbon.cloud.base.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // 时间格式化标准：你可以自由改成你想要的格式，如 yyyy-MM-dd HH:mm:ss
    private static final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // ====================== 1. 全局不返回 null 字段 ======================
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略null值
//        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
//        objectMapper.setDefaultPropertyInclusion(
//                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL));

//        // ====================== 2. 标准化输出 OffsetDateTime ======================
//        JavaTimeModule timeModule = new JavaTimeModule();
//
//        // 序列化（输出格式）
//        timeModule.addSerializer(OffsetDateTime.class,
//                                 new OffsetDateTimeSerializer(OFFSET_DATE_TIME_FORMATTER, false, true));
//
//        // 反序列化（接收参数格式）
//        timeModule.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer(OFFSET_DATE_TIME_FORMATTER));
//
//        objectMapper.registerModule(timeModule);
//
//        // ====================== 可选配置 ======================
//        // 关闭把日期写成时间戳（必须关，否则输出数字）
//        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        // 忽略未知字段
//        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
//        false);

        return objectMapper;
    }
}