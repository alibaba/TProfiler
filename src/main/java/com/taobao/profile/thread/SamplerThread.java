/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.thread;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.taobao.profile.Manager;
import com.taobao.profile.config.ProfConfig;
import com.taobao.profile.utils.DailyRollingFileWriter;

/**
 * 调用栈采样线程
 * 
 * @author shutong.dy
 * @since 2012-1-7
 */
public class SamplerThread extends Thread {

	/**
	 * log writer
	 */
	private final DailyRollingFileWriter fileWriter;
	/**
	 * 
	 */
	private final int samplerIntervalTime;

	/**
	 * 线程构造器
	 * 
	 * @param config
	 */
	public SamplerThread(ProfConfig config) {
		// 读取配置
		fileWriter = new DailyRollingFileWriter(config.getSamplerFilePath());
		samplerIntervalTime = config.getSamplerIntervalTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			while (true) {
				if (Manager.instance().canDump()) {
					Date date = new Date();
					Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
					for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
						Thread thread = entry.getKey();
						StringBuilder sb = new StringBuilder();
						sb.append("Thread\t");
						sb.append(thread.getId());
						sb.append("\t");
						sb.append(thread.getName());
						sb.append("\t");
						sb.append(thread.getState());
						sb.append("\t");
						sb.append(date);
						sb.append("\n");
						fileWriter.append(sb.toString());
						for (StackTraceElement element : entry.getValue()) {
							fileWriter.append(element.toString());
							fileWriter.append("\n");
						}
						fileWriter.flushAppend();
					}
				}
				// sleep
				TimeUnit.SECONDS.sleep(samplerIntervalTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				fileWriter.closeFile();
			}
		}
	}
}
