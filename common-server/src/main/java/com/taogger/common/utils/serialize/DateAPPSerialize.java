package com.taogger.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.taogger.common.utils.LocalDateTimeUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 时间样式显示转换
 * @author taogger
 * @date 2022/9/1 9:25
 */
public class DateAPPSerialize extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(LocalDateTimeUtils.appFormatTime(dateTime));
    }
}
