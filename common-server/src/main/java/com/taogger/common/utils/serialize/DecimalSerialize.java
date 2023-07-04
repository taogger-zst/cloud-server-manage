package com.taogger.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author taogger
 * @description 小数保留2位返回给前端序列化器
 * @date 2023-06-06 11:31
 **/
public class DecimalSerialize extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            BigDecimal bigDecimal = new BigDecimal(value.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
            jsonGenerator.writeString(bigDecimal.toString());
        }
    }
}
