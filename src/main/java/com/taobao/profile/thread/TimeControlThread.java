/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.thread;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.taobao.profile.Manager;
import com.taobao.profile.Profiler;
import com.taobao.profile.config.ProfConfig;
import com.taobao.profile.runtime.MethodCache;

/**
 * 开始时间结束时间控制线程
 * 
 * @author shutong.dy
 * @since 2012-1-12
 */
public class TimeControlThread extends Thread {
	/**
	 * 
	 */
	private Object lock = new Object();

	/**
	 * 
	 */
	private InnerControTime startTime;
	/**
	 * 
	 */
	private InnerControTime endTime;

	/**
	 * @param start
	 * @param end
	 */
	public TimeControlThread(ProfConfig config) {
		startTime = parse(config.getStartProfTime());
		endTime = parse(config.getEndProfTime());
	}

	/**
	 * @param time
	 * @return
	 */
	public long waitTime(InnerControTime time) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, time.getSecond());
		long startupTime = cal.getTimeInMillis();
		long _waitTime = startupTime - System.currentTimeMillis();
		return _waitTime;
	}

	/**
	 * @param time
	 * @return
	 */
	public long nextStartTime(InnerControTime time) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, time.getHour());
		cal.set(Calendar.MINUTE, time.getMinute());
		cal.set(Calendar.SECOND, time.getSecond());
		long startupTime = cal.getTimeInMillis();
		long _waitTime = startupTime - System.currentTimeMillis();
		return _waitTime;
	}

	/**
	 * @param time
	 */
	private void await(long time) {
		synchronized (lock) {
			try {
				lock.wait(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		if (startTime == null || endTime == null) {
			return;
		}
		try {
			// delay 30s
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			long time = waitTime(startTime);
			if (time > 0) {
				await(time);
			} else {
				time = waitTime(endTime);
				if (time > 0) {
					Profiler.clearData();
					Manager.instance().setCanProfile(true);
					await(time);
					MethodCache.flushMethodData();
				} else {
					Manager.instance().setCanProfile(false);
					time = nextStartTime(startTime);
					await(time);
				}
			}
		}
	}

	/**
	 * @param time
	 * @return
	 */
	public InnerControTime parse(String time) {
		if (time == null) {
			return null;
		} else {
			time = time.trim();
			String[] _time = time.split(":");
			if (_time.length == 3) {
				try {
					int hour = Integer.valueOf(_time[0]);
					int minute = Integer.valueOf(_time[1]);
					int second = Integer.valueOf(_time[2]);
					InnerControTime inner = new InnerControTime();
					inner.setHour(hour);
					inner.setMinute(minute);
					inner.setSecond(second);
					return inner;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	/**
	 * 映射时间类
	 * 
	 * @author shutong.dy
	 * @since 2012-1-12
	 */
	private class InnerControTime {
		private int hour;
		private int minute;
		private int second;

		public int getHour() {
			return hour;
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public int getMinute() {
			return minute;
		}

		public void setMinute(int minute) {
			this.minute = minute;
		}

		public int getSecond() {
			return second;
		}

		public void setSecond(int second) {
			this.second = second;
		}
	}
}
