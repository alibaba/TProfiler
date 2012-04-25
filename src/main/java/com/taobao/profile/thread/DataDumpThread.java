/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.thread;

import java.util.concurrent.TimeUnit;

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.config.ProfConfig;
import com.taobao.profile.runtime.ProfStack;
import com.taobao.profile.runtime.ThreadData;
import com.taobao.profile.utils.DailyRollingFileWriter;

/**
 * 将性能分析数据写到log中
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class DataDumpThread extends Thread {
	/**
	 * log writer
	 */
	private DailyRollingFileWriter fileWriter;
	/**
	 * 默认profile时间(s)
	 */
	private int eachProfUseTime;
	/**
	 * 两次profile间隔时间(s)
	 */
	private int eachProfIntervalTime;

	/**
	 * 线程构造器
	 * 
	 * @param config
	 */
	public DataDumpThread(ProfConfig config) {
		// 读取用户配置
		fileWriter = new DailyRollingFileWriter(config.getLogFilePath());
		eachProfUseTime = config.getEachProfUseTime();
		eachProfIntervalTime = config.getEachProfIntervalTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// 写log文件头
		boolean nano = Manager.isNeedNanoTime();
		fileWriter.setLogHeadContent("##nano:" + nano + "\n");
		fileWriter.printLogHeadContent();
		fileWriter.flushAppend();

		while (true) {
			try {
				TimeUnit.SECONDS.sleep(eachProfUseTime);
				if (!Manager.instance().canDump()) {
					continue;
				}
				Manager.instance().setPauseProfile(true);
				// 等待暂停生效
				TimeUnit.MILLISECONDS.sleep(500);

				dumpProfileData();

				TimeUnit.SECONDS.sleep(eachProfIntervalTime);
				Manager.instance().setPauseProfile(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将profile数据写到log中
	 * 
	 * @return
	 */
	private void dumpProfileData() {
		ThreadData[] threadData = Profiler.threadProfile;
		for (int index = 0; index < threadData.length; index++) {
			ThreadData profilerData = threadData[index];
			if (profilerData == null) {
				continue;
			}
			ProfStack<long[]> profile = profilerData.profileData;
			while (profile.size() > 0) {
				long[] data = profile.pop();
				StringBuilder sb = new StringBuilder();
				// thread id
				sb.append(index);
				sb.append('\t');
				// stack number
				sb.append(data[1]);
				sb.append('\t');
				// method id
				sb.append(data[0]);
				sb.append('\t');
				// use time
				sb.append(data[2]);
				sb.append('\n');
				fileWriter.append(sb.toString());
			}
			fileWriter.flushAppend();
			profilerData.clear();
		}
	}
}
