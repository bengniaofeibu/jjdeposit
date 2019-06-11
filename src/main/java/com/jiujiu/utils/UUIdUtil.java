package com.jiujiu.utils;

import java.util.UUID;

/**
 * Created by t_hz on 2019/4/16.
 */
public class UUIdUtil {

    public static String getUuId(){
        return UUID.randomUUID().toString().replace("_","");
    }
}
