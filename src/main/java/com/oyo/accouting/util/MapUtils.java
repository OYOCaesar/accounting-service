package com.oyo.accouting.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangwei on 2018/8/21.
 */
public class MapUtils {
    public static Map<String,Object> createConditionMap(Object ...strs){
        Map map = new HashMap();
        for(int i = 0 ; i < strs.length ; i = i + 2){
            map.put(strs[i].toString(),strs[i+1]);
        }
        return map;
    }
}
