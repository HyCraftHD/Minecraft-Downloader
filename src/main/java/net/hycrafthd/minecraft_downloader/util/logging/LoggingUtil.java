package net.hycrafthd.minecraft_downloader.util.logging;

import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.util.StackLocatorUtil;

import net.hycrafthd.minecraft_downloader.Main;

public class LoggingUtil {
	
	public static void redirectPrintStreams() {
		System.setErr(new LoggingPrintStream(Main.LOGGER, Level.ERROR, MarkerManager.getMarker("STDERR"), System.err));
		System.setOut(new LoggingPrintStream(Main.LOGGER, Level.INFO, MarkerManager.getMarker("STDOUT"), System.out));
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			Main.LOGGER.atFatal().withThrowable(throwable).log("A fatal exception occured :");
		});
	}
	
	private static class LoggingPrintStream extends PrintStream {
		
		private final Logger logger;
		private final Level level;
		private final Marker marker;
		
		public LoggingPrintStream(Logger logger, Level level, Marker marker, PrintStream printStream) {
			super(printStream);
			this.logger = logger;
			this.level = level;
			this.marker = marker;
		}
		
		private void log(String string) {
			logger.atLevel(level).withMarker(marker).log("[{}] {}", StackLocatorUtil.getStackTraceElement(3), string);
		}
		
		// Catch most common methods for print stream
		
		@Override
		public void println(String value) {
			log(value);
		}
		
		@Override
		public void println(Object value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(boolean value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(char value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(int value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(long value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(float value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(double value) {
			log(String.valueOf(value));
		}
		
		@Override
		public void println(char[] value) {
			log(String.valueOf(value));
		}
	}
}
