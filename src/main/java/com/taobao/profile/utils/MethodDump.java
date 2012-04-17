/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.utils;

import java.util.Vector;

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.runtime.MethodCache;
import com.taobao.profile.runtime.MethodInfo;

/**
 * 将方法信息写出到log文件中
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class MethodDump {
	/**
	 * 写出方法信息
	 */
	public static void flushMethodData() {
		DailyRollingFileWriter fileWriter = new DailyRollingFileWriter(Manager.METHOD_LOG_PATH);

		fileWriter.append("instrumentclass:");
		fileWriter.append(String.valueOf(Profiler.instrumentClassCount));
		fileWriter.append(" instrumentmethod:");
		fileWriter.append(String.valueOf(Profiler.instrumentMethodCount));
		fileWriter.append("\n");

		Vector<MethodInfo> vector = MethodCache.mCacheMethods;
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
		fileWriter.closeFile();
	}
}
