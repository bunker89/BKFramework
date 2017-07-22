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
		public void err(String title, String text) {
			String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
			System.err.println("******** error -> " + tag + "********");
			StackTraceElement []traces = Thread.currentThread().getStackTrace();
			for (StackTraceElement trace : traces) {
				System.err.println("\t" + trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
			}
			System.err.println();
		}
	};
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	synchronized public static void logging(String tag, String log) {
		mLog.log(tag, log);
	}

	synchronized public static void err(String tag, String log) {
		mLog.err(tag, log);
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