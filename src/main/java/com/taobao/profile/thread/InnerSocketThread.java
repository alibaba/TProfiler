/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.taobao.profile.Manager;
import com.taobao.profile.runtime.MethodCache;

/**
 * 对外提供Socket开关
 * 
 * @author shutong.dy
 * @since 2012-1-11
 */
public class InnerSocketThread extends Thread {
	/**
	 * server
	 */
	private ServerSocket socket;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			socket = new ServerSocket(Manager.PORT);
			while (true) {
				Socket child = socket.accept();

				child.setSoTimeout(5000);

				String command = read(child.getInputStream());

				if (Manager.START.equals(command)) {
					Manager.instance().setSwitchFlag(true);
				} else if (Manager.STATUS.equals(command)) {
					write(child.getOutputStream());
				} else if (Manager.FLUSHMETHOD.equals(command)) {
					MethodCache.flushMethodData();
				} else {
					Manager.instance().setSwitchFlag(false);
				}
				child.close();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取输入流
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private String read(InputStream in) throws IOException {
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
	 * 输出状态
	 * 
	 * @param os
	 * @throws IOException
	 */
	private void write(OutputStream os) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(os);
		if (Manager.instance().getSwitchFlag()) {
			out.write("running".getBytes());
		} else {
			out.write("stop".getBytes());
		}
		out.write('\r');
		out.flush();
	}
}
