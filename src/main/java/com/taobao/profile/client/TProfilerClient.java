/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TProfiler客户端,用来远程打开 关闭及查看状态
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class TProfilerClient {

	/**
	 * 远程打开
	 * 
	 * @param server
	 *            ip
	 */
	public static void start(String server) {
		doSend("start", server);
	}

	/**
	 * 远程关闭
	 * 
	 * @param server
	 *            ip
	 */
	public static void stop(String server) {
		doSend("stop", server);
	}

	/**
	 * 获取状态
	 * 
	 * @param server
	 * @return
	 */
	public static String status(String server) {
		return getStatus("status", server);
	}

	/**
	 * 远程刷出方法数据
	 * 
	 * @param server
	 *            ip
	 */
	public static void flushMethod(String server) {
		doSend("flushmethod", server);
	}

	/**
	 * 建立远程连接并发送命令
	 * 
	 * @param command
	 * @param server
	 */
	private static void doSend(String command, String server) {
		Socket socket = null;
		try {
			socket = new Socket(server, 50000);
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream out = new BufferedOutputStream(os);
			out.write(command.getBytes());
			out.write('\r');
			out.flush();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 建立远程连接并发送命令
	 * 
	 * @param command
	 * @param server
	 * @return 0:运行状态 1:停止状态
	 */
	private static String getStatus(String command, String server) {
		Socket socket = null;
		try {
			socket = new Socket(server, 50000);
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream out = new BufferedOutputStream(os);
			out.write(command.getBytes());
			out.write('\r');
			out.flush();
			return read(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 读取输入流
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream in) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		StringBuffer sb = new StringBuffer();
		while (true) {
			char c = (char) bin.read();
			if (c == '\r') {
				break;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: <server ip> <command[start/stop/status]>");
			return;
		}
		if (args[1].toLowerCase().equals("start")) {
			start(args[0]);
		} else if (args[1].toLowerCase().equals("stop")) {
			stop(args[0]);
		} else if (args[1].toLowerCase().equals("flushmethod")) {
			flushMethod(args[0]);
		} else {
			System.out.println(status(args[0]));
		}
	}
}
