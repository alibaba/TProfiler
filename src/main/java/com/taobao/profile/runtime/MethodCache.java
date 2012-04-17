/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.runtime;

import java.util.Vector;

/**
 * 方法名缓存,用ID代替方法名进行剖析,提升性能
 * 
 * @author luqi
 * @since 2010-6-23
 */
public class MethodCache {

	/**
	 * 方法缓存默认大小
	 */
	private static final int INIT_CACHE_SIZE = 10240;
	/**
	 * 方法名缓存
	 */
	public static Vector<MethodInfo> mCacheMethods = new Vector<MethodInfo>(INIT_CACHE_SIZE);

	/**
	 * 占位并生成方法ID
	 * 
	 * @return
	 */
	public synchronized static int Request() {
		mCacheMethods.add(new MethodInfo());
		return mCacheMethods.size() - 1;
	}

	/**
	 * 更新文件名
	 * 
	 * @param id
	 * @param fileName
	 */
	public synchronized static void UpdateFileName(int id, String fileName) {
		mCacheMethods.get(id).setMFileName(fileName);
	}

	/**
	 * 更新行号
	 * 
	 * @param id
	 * @param linenum
	 */
	public synchronized static void UpdateLineNum(int id, int linenum) {
		mCacheMethods.get(id).setMLineNum(linenum);
	}

	/**
	 * 更新类名方法名
	 * 
	 * @param id
	 * @param className
	 * @param methodName
	 */
	public synchronized static void UpdateMethodName(int id, String className, String methodName) {
		mCacheMethods.get(id).setMClassName(className);
		mCacheMethods.get(id).setMMethodName(methodName);
	}

	/**
	 * 取得方法名
	 * 
	 * @param id
	 * @return
	 */
	public static String getClasssMethodInfo(int id) {
		MethodInfo method = mCacheMethods.get(id);
		return method.toString();
	}

}
