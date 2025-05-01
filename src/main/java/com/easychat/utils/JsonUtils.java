package com.easychat.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static SerializerFeature[] FEATURES = new SerializerFeature[] { SerializerFeature.WriteMapNullValue};

    public static <T> T convertJson2Obj(String json, Class<T> clazz) throws BusinessException {
        try{
            return JSONObject.parseObject(json,clazz);
        } catch (Exception e) {
            logger.error("convertJson2Obj异常， json:{}",json);
            throw new BusinessException(ResponseCodeEnum.CODE_601);
        }
    }
}
