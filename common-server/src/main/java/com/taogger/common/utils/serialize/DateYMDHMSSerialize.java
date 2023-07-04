package com.taogger.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.taogger.common.utils.LocalDateTimeUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 日期转换类
 * @author taogger
 * @date 2022/8/12 15:39
 */
public class DateYMDHMSSerialize extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(LocalDateTimeUtils.getYMDHms(dateTime));
    }
}
