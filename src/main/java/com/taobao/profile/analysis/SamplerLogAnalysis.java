/**
 * (C) 2011-2012 Alibaba Group Holding Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 */
package com.taobao.profile.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析Sampler生成的Log
 * 
 * @author shutong.dy
 * @since 2012-2-9
 */
public class SamplerLogAnalysis {

	private String logPath;
	private Map<String, Integer> originalMethodMap = new HashMap<String, Integer>();
	private List<CompareObject> methodList = new ArrayList<CompareObject>();

	private Map<String, Integer> originalThreadMap = new HashMap<String, Integer>();
	private List<CompareObject> threadList = new ArrayList<CompareObject>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: <tsampler.log path> <samplermethodresult.log path> <samplerthreadresult.log path>");
            return;
        }
		SamplerLogAnalysis analysis = new SamplerLogAnalysis(args[0]);
		analysis.reader();
		analysis.printMethodResult(args[1]);
		analysis.printThreadResult(args[2]);
	}

	/**
	 * @param inPath
	 */
	public SamplerLogAnalysis(String inPath) {
		this.logPath = inPath;
	}

	/**
	 * 读取log,并解析
	 */
	private void reader() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(logPath)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("Thread\t")) {
					Integer tmpCount = originalThreadMap.get(line);
					if (tmpCount == null) {
						originalThreadMap.put(line, 1);
					} else {
						originalThreadMap.put(line, tmpCount.intValue() + 1);
					}
				} else {
					if (line.startsWith("com") || line.startsWith("org")) {
						Integer tmpCount = originalMethodMap.get(line);
						if (tmpCount == null) {
							originalMethodMap.put(line, 1);
						} else {
							originalMethodMap.put(line, tmpCount.intValue() + 1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 取出结果,供分析程序调用
	 * 
	 * @return
	 */
	public List<CompareObject> getMethodResult() {
		for (Map.Entry<String, Integer> entry : originalMethodMap.entrySet()) {
			CompareObject obj = new CompareObject();
			obj.setMethodName(entry.getKey());
			obj.setCount(entry.getValue());
			methodList.add(obj);
		}
		Collections.sort(methodList);
		return methodList;
	}

	/**
	 * 取出结果,供分析程序调用
	 * 
	 * @return
	 */
	public List<CompareObject> getThreadResult() {
		for (Map.Entry<String, Integer> entry : originalThreadMap.entrySet()) {
			CompareObject obj = new CompareObject();
			String[] tmp = entry.getKey().split("\t");
			obj.setThreadId(tmp[1]);
			obj.setThreadName(tmp[2]);
			obj.setThreadState(tmp[3]);
			obj.setCount(entry.getValue());
			threadList.add(obj);
		}
		Collections.sort(threadList);
		return threadList;
	}

	/**
	 * 输出分析结果
	 * 
	 * @param outPath
	 */
	public void printMethodResult(String outPath) {
		for (Map.Entry<String, Integer> entry : originalMethodMap.entrySet()) {
			CompareObject obj = new CompareObject();
			obj.setMethodName(entry.getKey());
			obj.setCount(entry.getValue());
			methodList.add(obj);
		}
		Collections.sort(methodList);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outPath));
			int count = 0;
			for (CompareObject entry : methodList) {
				writer.write(entry.getMethodName());
				writer.write("\t");
				writer.write(String.valueOf(entry.getCount()));
				writer.write("\r");

				count++;
				if ((count % 30) == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 输出分析结果
	 * 
	 * @param outPath
	 */
	public void printThreadResult(String outPath) {
		for (Map.Entry<String, Integer> entry : originalThreadMap.entrySet()) {
			CompareObject obj = new CompareObject();
			String[] tmp = entry.getKey().split("\t");
			obj.setThreadId(tmp[1]);
			obj.setThreadName(tmp[2]);
			obj.setThreadState(tmp[3]);
			obj.setCount(entry.getValue());
			threadList.add(obj);
		}
		Collections.sort(threadList);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outPath));
			int count = 0;
			for (CompareObject entry : threadList) {
				writer.write(entry.getThreadId());
				writer.write("\t");
				writer.write(entry.getThreadName());
				writer.write("\t");
				writer.write(entry.getThreadState());
				writer.write("\t");
				writer.write(String.valueOf(entry.getCount()));
				writer.write("\r");

				count++;
				if ((count % 30) == 0) {
					writer.flush();
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
