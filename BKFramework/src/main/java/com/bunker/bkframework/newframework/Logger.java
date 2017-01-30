package com.bunker.bkframework.newframework;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	synchronized public static void logging(String title, String log) {
		String tag = dateFormat.format(new Date()) + ":" + title + " : " + log;
		System.out.println(tag);
	}
	
	synchronized public static void err(String title, String log) {
		String tag = dateFormat.format(new Date()) + ":" + title + " : " + log;
		System.err.println("******** error -> " + tag + "********");
		StackTraceElement []traces = Thread.currentThread().getStackTrace();
		for (StackTraceElement trace : traces) {
			System.err.println("\t" + trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
		}
		System.err.println();
	}
	
	synchronized public static void warning(String title, String log) {
		String tag = dateFormat.format(new Date()) + ":" + title + " : " + log;
		System.out.println("*** warning -> " + tag + "***");
	}	
	
	public static void print(String tag) {
		System.out.println(tag );
	}
	
	public static void dumpByte(byte []dump, int offset, int size) {
		System.out.print("dump:");
		for (int i = 0; i < size; i++) {
			System.out.print(dump[i + offset] + ", ");
		}
		System.out.println("");
	}
}