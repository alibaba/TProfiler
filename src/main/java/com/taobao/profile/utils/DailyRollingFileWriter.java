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
	 * 
	 */
	private String fileName;
	/**
	 * 
	 */
	private String rollingFileName;
	/**
	 * 
	 */
	private BufferedWriter bufferedWriter;

	/**
	 * 
	 */
	private String logHeadContent = "";

	/**
	 * 
	 */
	private RollingCalendar rollingCalendar = new RollingCalendar();
	/**
	 * 
	 */
	private SimpleDateFormat sdf = new SimpleDateFormat("'.'yyyy-MM-dd");
	/**
	 * 
	 */
	private long nextRollingTime = rollingCalendar.getNextRollingMillis(new Date());

	/**
	 * @param filePath
	 */
	public DailyRollingFileWriter(String filePath) {
		fileName = filePath;
		createWriter(filePath, 8 * 1024);
		rollingFileName = fileName + sdf.format(new Date());
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
			nextRollingTime = rollingCalendar.getNextRollingMillis(new Date());
			rolling();
			printLogHeadContent();
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
	 * 
	 */
	private void rolling() {
		Date now = new Date();
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
		createWriter(fileName, 8 * 1024);
		rollingFileName = datedFilename;
	}

	/**
	 * @param filename
	 * @param bufferSize
	 */
	private void createWriter(String filename, int bufferSize) {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filename), bufferSize);
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
