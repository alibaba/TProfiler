/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */

package com.taobao.profile.utils;

import java.util.Map;

/**
 * 提供常用的一些工具方法
 * @author 刘永伟(manlge)
 * @version 1.0
 * @since 2013-08-18
 */
public class Utilities {

  private Utilities() {
  }
  
  /**
   * 变量替换,将以${user.home}类似的变量替换成具体的值
   * @param source 源字符串，如：${user.dir}/${user.language}/tprofiler.log
   * @param context 上下文，用于查找变量的具体值
   * @return 替换过的字符串
   * @throws VariableNotFoundException
   * @author manlge
   */
  public static String repleseVariables(String source, Map<Object, Object> context) throws VariableNotFoundException{
    if (source == null){
      throw new IllegalArgumentException("source can't be null");
    }

    if (context == null){
      throw new IllegalArgumentException("context can't be null");
    }

    //从后向前查找    
    int p = source.lastIndexOf('}');
    while (p != -1){
      int p1 = source.lastIndexOf("${");
      //没有找到匹配
      if (p1 == -1){
        return source;
      }
      
      String key = source.substring(p1 + 2, p); //+2 是跳过${
      if (!context.containsKey(key)){
        throw new VariableNotFoundException("variable " + key + " not found");
      }
      String value = String.valueOf(context.get(key));
      String start = source.substring(0, p1);
      String end = source.substring(p + 1);
      source = start + value + end;
      p = source.lastIndexOf('}');
    }
    
    return source;
  }
}
