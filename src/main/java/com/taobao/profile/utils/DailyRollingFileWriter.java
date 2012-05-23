/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 将log写出,具备日滚功能
 * 
 * @author xiaodu
 * @since 2010-6-23
 */
public class DailyRollingFileWriter {
	/**
	 * 文件名
	 */
	private String fileName;
	/**
	 * 日滚文件名
	 */
	private String rollingFileName;
	/**
	 * BufferedWriter实例
	 */
	private BufferedWriter bufferedWriter;

	/**
	 * 日志头
	 */
	private String logHeadContent = "";

	/**
	 * 获取下次滚动时间的Calendar
	 */
	private RollingCalendar rollingCalendar = new RollingCalendar();
	/**
	 * 格式化工具
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("'.'yyyy-MM-dd");
	/**
	 * 下次的滚动时间
	 */
	private long nextRollingTime = rollingCalendar.getNextRollingMillis(new Date());

	/**
	 * @param filePath
	 */
	public DailyRollingFileWriter(String filePath) {
		fileName = filePath;
		createWriter(filePath);
		Date now = new Date();
		rollingFileName = fileName + sdf.format(now);

		// 最后修改时间不是今天,做滚动
		File file = new File(filePath);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date lastModifiedDate = new Date(file.lastModified());
		String lastModified = dateFormat.format(lastModifiedDate);
		if (!lastModified.equals(dateFormat.format(now))) {
			rollingFileName = fileName + sdf.format(lastModifiedDate);
			rolling(now);
		}
	}

	/**
	 * @param head
	 */
	public void setLogHeadContent(String head) {
		logHeadContent = head;
	}

	/**
	 * 
	 */
	public void printLogHeadContent() {
		subappend(logHeadContent);
	}

	/**
	 * @param log
	 */
	public void append(String log) {
		long time = System.currentTimeMillis();
		if (time > nextRollingTime) {
			Date now = new Date();
			nextRollingTime = rollingCalendar.getNextRollingMillis(now);
			rolling(now);
		}
		subappend(log);
	}

	/**
	 * 
	 */
	public void flushAppend() {
		if (bufferedWriter != null) {
			try {
				bufferedWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param log
	 */
	private void subappend(String log) {
		try {
			bufferedWriter.write(log);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void closeFile() {
		if (bufferedWriter != null) {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param now
	 */
	private void rolling(Date now) {
		String datedFilename = fileName + sdf.format(now);
		if (rollingFileName.equals(datedFilename)) {
			return;
		}
		closeFile();
		File target = new File(rollingFileName);
		if (target.exists()) {
			target.delete();
		}

		File file = new File(fileName);
		file.renameTo(target);
		createWriter(fileName);
		rollingFileName = datedFilename;
	}

	/**
	 * @param filename
	 */
	private void createWriter(String filename) {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filename, true), 8 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author xiaodu
	 * @since 2010-6-23
	 */
	private class RollingCalendar {

		/**
		 * @param now
		 * @return
		 */
		public long getNextRollingMillis(Date now) {
			return getNextRollingDate(now).getTime();
		}

		/**
		 * 取得下一次滚动时间
		 * 
		 * @param now
		 * @return
		 */
		private Date getNextRollingDate(Date now) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(now);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, 1);
			return cal.getTime();
		}
	}
}
