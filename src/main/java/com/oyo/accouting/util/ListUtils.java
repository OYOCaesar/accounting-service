package com.oyo.accouting.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangwei on 2018/8/21.
 */
public class ListUtils {


    public static <T> List<List<T>> partition(List<T> dataList, int itemSize){
       if(CollectionUtils.isEmpty(dataList)) return null;
       int beginIndex = 0;
       int endIndex = beginIndex + itemSize;
       int partitionSize = dataList.size() % itemSize == 0 ?  dataList.size() / itemSize : dataList.size() / itemSize + 1;
       List<List<T>> resultList = new ArrayList<List<T>>();
       int size = dataList.size();
       for(int index = 0 ; index < partitionSize ; index ++){
           beginIndex += index * itemSize;
           endIndex += index * itemSize;
           if(size < endIndex){
               resultList.add(dataList.subList(beginIndex, size));
           }else{
               resultList.add(dataList.subList(beginIndex,endIndex));
           }
       }
       return  resultList;
    }

    public static <T> List<T> cutList(List<T> dataList, int beginIndex, int endIndex){
        List<T> resultList = new ArrayList<T>();
        for(int index = beginIndex; index <= endIndex ; index++){
            resultList.add(dataList.get(index));
        }
        return resultList;
    }

    /**
     * 分页方法
     *
     * @param list
     *            源数据
     * @param pageNum
     *            当前页
     * @param pageSize
     *            每页显示几条
     * @param totalPage
     *            总页数
     * @return
     */
    public static <T> List<T> getPageList(List<T> list, int pageNum, int pageSize,
                                   int totalPage) {

        int fromIndex = 0; // 从哪里开始截取
        int toIndex = 0; // 截取几个
        if (list == null || list.size() == 0)
            return new ArrayList<T>();
        // 当前页小于或等于总页数时执行
        if (pageNum <= totalPage && pageNum != 0) {
            fromIndex = (pageNum - 1) * pageSize;

            if (pageNum == totalPage) {
                toIndex = list.size();
            } else {
                toIndex = pageNum * pageSize;
            }
        }
        return list.subList(fromIndex, toIndex);
    }
}
