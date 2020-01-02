package com.bunker.bkframework.newframework;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static Log mLog = new Log() {

		@Override
		public void warning(String title, String text) {
			String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
			System.out.println("*** warning -> " + tag + "***");
		}

		@Override
		public void log(String title, String text) {
			String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
			System.out.println(tag);
		}

		@Override
		public String err(String title, String text) {
			String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
			System.err.println("******** error -> " + tag + "********");
			StackTraceElement []traces = Thread.currentThread().getStackTrace();
			for (StackTraceElement trace : traces) {
				System.err.println("\t" + trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
			}
			System.err.println();
			return "noid";
		}

		@Override
		public String err(String tag, String text, Exception e) {
			StackTraceElement []traces = e.getStackTrace();
			System.err.print("\t" + e.getMessage());
			for (StackTraceElement trace : traces) {
				System.err.println("\t" + trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
			}
			System.err.println(text);
			return "noid";
		}
	};
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

	synchronized public static void logging(String tag, String log) {
		mLog.log(tag, log);
	}

	synchronized public static String err(String tag, String log) {
		return mLog.err(tag, log);
	}

	synchronized public static String err(String tag, String log, Exception e) {
		return mLog.err(tag, log, e);
	}

	synchronized public static void warning(String tag, String log) {
		mLog.warning(tag, log);
	}

	public static void print(String tag) {
		System.out.println(tag);
	}

	public static void dumpByte(byte []dump, int offset, int size) {
		System.out.print("dump:");
		for (int i = 0; i < size; i++) {
			System.out.print(dump[i + offset] + ", ");
		}
		System.out.println("");
	}
}