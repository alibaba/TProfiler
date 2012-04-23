/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile;

import java.util.concurrent.atomic.AtomicBoolean;

import com.taobao.profile.config.ProfConfig;
import com.taobao.profile.config.ProfFilter;
import com.taobao.profile.thread.DataDumpThread;
import com.taobao.profile.thread.InnerSocketThread;
import com.taobao.profile.thread.SamplerThread;
import com.taobao.profile.thread.TimeControlThread;

/**
 * 管理类,单例实现
 * 
 * @author shutong.dy
 * @since 2012-1-9
 */
public class Manager {
	/**
	 * 默认配置文件
	 */
	private static final String DEFAULT_CONFIG = "profile";

	/**
	 * 远程连接端口
	 */
	public static final int PORT = 50000;
	/**
	 * 开始命令
	 */
	public static final String START = "start";
	/**
	 * 结束命令
	 */
	public static final String STOP = "stop";
	/**
	 * 获取状态命令
	 */
	public static final String STATUS = "status";
	/**
	 * 远程刷出方法数据
	 */
	public static final String FLUSHMETHOD = "flushmethod";
	/**
	 * 是否用纳秒采集
	 */
	private static boolean NEED_NANO_TIME;
	/**
	 * 是否忽略get/set方法
	 */
	private static boolean IGNORE_GETSET_METHOD;
	/**
	 * 默认方法log位置
	 */
	public static String METHOD_LOG_PATH;

	/**
	 * Manager
	 */
	private static Manager manager = new Manager();

	/**
	 * profile配置
	 */
	private ProfConfig profConfig;

	/**
	 * 当前时间大于开始时间小于结束时间,则可以profile
	 */
	private AtomicBoolean canProfile = new AtomicBoolean(false);
	/**
	 * 暂停profile,将log写到硬盘
	 */
	private AtomicBoolean pauseProfile = new AtomicBoolean(true);
	/**
	 * 远程开始或结束的开关
	 */
	private AtomicBoolean switchProfile = new AtomicBoolean(true);

	/**
	 * 开始时间结束时间控制线程
	 */
	private final TimeControlThread controlThread;
	/**
	 * 对外提供Socket开关
	 */
	private final InnerSocketThread socketThread;
	/**
	 * 将性能分析数据写出到磁盘
	 */
	private final DataDumpThread dumpThread;

	/**
	 * 采样线程
	 */
	private final SamplerThread samplerThread;

	/**
	 * 私有构造器
	 */
	private Manager() {
		profConfig = new ProfConfig(DEFAULT_CONFIG);
		NEED_NANO_TIME = profConfig.isNeedNanoTime();
		IGNORE_GETSET_METHOD = profConfig.isIgnoreGetSetMethod();
		METHOD_LOG_PATH = profConfig.getMethodFilePath();

		setProfFilter(profConfig);

		controlThread = new TimeControlThread(profConfig);
		controlThread.setName("TProfiler-TimeControl");
		controlThread.setDaemon(true);

		socketThread = new InnerSocketThread();
		socketThread.setName("TProfiler-InnerSocket");
		socketThread.setDaemon(true);

		dumpThread = new DataDumpThread(profConfig);
		dumpThread.setName("TProfiler-DataDump");
		dumpThread.setDaemon(false);

		samplerThread = new SamplerThread(profConfig);
		samplerThread.setName("TProfiler-Sampler");
		samplerThread.setDaemon(false);
	}

	/**
	 * @return
	 */
	public static Manager instance() {
		return manager;
	}

	/**
	 * @return the needNanoTime
	 */
	public static boolean isNeedNanoTime() {
		return NEED_NANO_TIME;
	}

	/**
	 * @return the ignoreGetSetMethod
	 */
	public static boolean isIgnoreGetSetMethod() {
		return IGNORE_GETSET_METHOD;
	}

	/**
	 * @param pause
	 */
	public void setPauseProfile(boolean pause) {
		this.pauseProfile.getAndSet(pause);
	}

	/**
	 * @param switchProfile
	 *            the switchProfile to set
	 */
	public void setSwitchProfile(boolean switchProfile) {
		this.switchProfile.getAndSet(switchProfile);
	}

	/**
	 * @return the switchProfile
	 */
	public boolean getSwitchProfile() {
		return switchProfile.get();
	}

	/**
	 * @param canProfile
	 *            the canProfile to set
	 */
	public void setCanProfile(boolean canProfile) {
		this.canProfile.getAndSet(canProfile);
	}

	/**
	 * 判断当前是否可以采集数据
	 * 
	 * @return
	 */
	public boolean canProfile() {
		return canProfile.get() && switchProfile.get() && !pauseProfile.get();
	}

	/**
	 * 判断当前是否可以dump数据
	 * 
	 * @return
	 */
	public boolean canDump() {
		return canProfile.get() && switchProfile.get();
	}

	/**
	 * 设置包名过滤器
	 * 
	 * @param profConfig
	 */
	private void setProfFilter(ProfConfig profConfig) {
		String include = profConfig.getIncludePackageStartsWith();
		if (include != null) {
			String[] _includes = include.split(";");
			for (String pack : _includes) {
				ProfFilter.addIncludeClass(pack);
			}
		}
		String exclude = profConfig.getExcludePackageStartsWith();
		if (exclude != null) {
			String[] _excludes = exclude.split(";");
			for (String pack : _excludes) {
				ProfFilter.addExcludeClass(pack);
			}
		}
	}

	/**
	 * 启动内部线程
	 * 
	 * @param config
	 */
	public void startupThread() {
		controlThread.start();
		socketThread.start();
		dumpThread.start();
		samplerThread.start();
	}
}
