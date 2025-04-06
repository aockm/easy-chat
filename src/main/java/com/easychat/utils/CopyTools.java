package com.easychat.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sourceList, Class<T> classz) {
        List<T> list = new ArrayList<>();
        for (S s : sourceList) {
            T t = null;
            try {
                t = classz.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s,t);
        }
        return list;
    }

    public static <T, S> T copy(S s, Class<T> classz) {
        T t = null;
        try {
            t = classz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s,t);
        return t;
    }
}
