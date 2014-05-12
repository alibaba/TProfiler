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

import com.taobao.profile.Manager;

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
     * @param port
     */
	public static void start(String server, int port) {
		doSend(Manager.START, server, port);
	}

	/**
	 * 远程关闭
     *
     * @param server
     * @param port
     */
	public static void stop(String server, int port) {
		doSend(Manager.STOP, server, port);
	}

	/**
	 * 获取状态
	 *
     * @param server
     * @param port
     * @return
     */
	public static String status(String server, int port) {
		return getStatus(Manager.STATUS, server, port);
	}

	/**
	 * 远程刷出方法数据
	 *
     * @param server
     * @param port
     */
	public static void flushMethod(String server, int port) {
		doSend(Manager.FLUSHMETHOD, server, port);
	}

	/**
	 * 建立远程连接并发送命令
	 *
     * @param command
     * @param server
     * @param port
     */
	private static void doSend(String command, String server, int port) {
		Socket socket = null;
		try {
			socket = new Socket(server, port);
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
     * @param port
     * @return
     */
	private static String getStatus(String command, String server, int port) {
		Socket socket = null;
		try {
			socket = new Socket(server, port);
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
        int i;
		while ((i = bin.read()) != -1) {
			char c = (char) i;
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
		if (args.length != 3) {
			System.err.println("Usage: <server ip> <server port> <command[start/stop/status/flushmethod]>");
			return;
		}
        int port = Integer.valueOf(args[1]);
		if (args[2].toLowerCase().equals(Manager.START)) {
			start(args[0], port);
		} else if (args[2].toLowerCase().equals(Manager.STOP)) {
			stop(args[0], port);
		} else if (args[2].toLowerCase().equals(Manager.FLUSHMETHOD)) {
			flushMethod(args[0], port);
		} else {
			System.out.println(status(args[0], port));
		}
	}
}
