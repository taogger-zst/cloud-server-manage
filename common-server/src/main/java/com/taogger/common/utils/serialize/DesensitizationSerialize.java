package com.taogger.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.taogger.common.utils.desensitization.Desensitization;
import com.taogger.common.utils.desensitization.DesensitizationType;
import com.taogger.common.utils.desensitization.DesensitizedUtils;

import java.io.IOException;

/**
 * 脱敏工具类
 * @author taogger
 * @date 2021/10/8 17:12
 */
public class DesensitizationSerialize extends JsonSerializer implements ContextualSerializer {

    private DesensitizationType type;

    public DesensitizationSerialize(){}

    public DesensitizationSerialize(final DesensitizationType type){
        this.type = type;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty beanProperty) throws JsonMappingException {
        if(beanProperty != null){
            //获取字段是否有脱敏注解，有则创建一个序列化对象，并调用serialize方法
            Desensitization desensitization = beanProperty.getAnnotation(Desensitization.class);
            if(desensitization == null){
                desensitization = beanProperty.getContextAnnotation(Desensitization.class);
            }
            // 如果定义了脱敏注解，就将需要脱敏的类型传入DesensitizationSerialize构造函数
            if(desensitization != null){
                return new DesensitizationSerialize(desensitization.value());
            }
            return provider.findValueSerializer(beanProperty.getType() , beanProperty);
        }
        return provider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        switch (this.type){
            case USER_ID:
                jsonGenerator.writeNumber(DesensitizedUtils.userId());
                break;
            case CHINESE_NAME:
                jsonGenerator.writeString(DesensitizedUtils.chineseName(String.valueOf(value)));
                break;
            case ID_CARD:
                jsonGenerator.writeString(DesensitizedUtils.idCardNum(String.valueOf(value),3,3));
                break;
            case FIXED_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.fixedPhone(String.valueOf(value)));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.mobilePhone(String.valueOf(value)));
                break;
            case ADDRESS:
                jsonGenerator.writeString(DesensitizedUtils.address(String.valueOf(value), 8));
                break;
            case EMAIL:
                jsonGenerator.writeString(DesensitizedUtils.email(String.valueOf(value)));
                break;
            case PASSWORD:
                jsonGenerator.writeString(DesensitizedUtils.password(String.valueOf(value)));
                break;
            default:
        }
    }
}
