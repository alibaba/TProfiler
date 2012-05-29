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

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.utils.DailyRollingFileWriter;

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
	private static Vector<MethodInfo> mCacheMethods = new Vector<MethodInfo>(INIT_CACHE_SIZE);
	
	/**
	 * 方法名writer
	 */
	private static DailyRollingFileWriter fileWriter = new DailyRollingFileWriter(Manager.METHOD_LOG_PATH);

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
	 * @param fileName
	 * @param className
	 * @param methodName
	 */
	public synchronized static void UpdateMethodName(int id, String fileName, String className, String methodName) {
		MethodInfo methodInfo = mCacheMethods.get(id);
		methodInfo.setMFileName(fileName);
		methodInfo.setMClassName(className);
		methodInfo.setMMethodName(methodName);
	}

	/**
	 * 写出方法信息
	 */
	public synchronized static void flushMethodData() {
		fileWriter.append("instrumentclass:");
		fileWriter.append(Profiler.instrumentClassCount.toString());
		fileWriter.append(" instrumentmethod:");
		fileWriter.append(Profiler.instrumentMethodCount.toString());
		fileWriter.append("\n");

		Vector<MethodInfo> vector = mCacheMethods;
		int size = vector.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(i);
			sb.append(' ');
			sb.append(vector.get(i).toString());
			sb.append('\n');
			fileWriter.append(sb.toString());
			sb.setLength(0);
			if ((i % 50) == 0) {
				fileWriter.flushAppend();
			}
		}
		fileWriter.flushAppend();
	}
}
