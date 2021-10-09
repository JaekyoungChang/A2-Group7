package cinco.ticket;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.IllegalFormatException;

public class ConsoleManager {

	public static class ConsoleException extends Exception {

		private static final long serialVersionUID = 1L;

		public ConsoleException(final String message) {
			super(message);
		}
	}

	public static abstract class TextDevice {
		public abstract TextDevice printf(String fmt, Object... params) throws ConsoleException;

		public abstract String readLine() throws ConsoleException;

		public abstract char[] readPassword() throws ConsoleException;

		public abstract Reader reader();

		public abstract PrintWriter writer();
	}

	public static class CharacterDevice extends TextDevice {

		private final BufferedReader reader;
		private final PrintWriter writer;

		public CharacterDevice(final BufferedReader reader, final PrintWriter writer) {
			this.reader = reader;
			this.writer = writer;
		}

		public TextDevice printf(String fmt, Object... params) throws ConsoleException {
			try {
				writer.printf(fmt, params);
			} catch (final IllegalFormatException | NullPointerException e) {
				throw new ConsoleException(e.getMessage());
			}
			return this;
		}

		public String readLine() throws ConsoleException {
			try {
				return reader.readLine();
			} catch (final IOException e) {
				throw new ConsoleException(e.getMessage());
			}
		}

		public char[] readPassword() throws ConsoleException {
			try {
				return reader.readLine().toCharArray();
			} catch (final IOException e) {
				throw new ConsoleException(e.getMessage());
			}
		}

		public Reader reader() {
			return reader();
		}

		public PrintWriter writer() {
			return writer();
		}
	}

	public static class ConsoleDevice extends TextDevice {

		private final Console console;

		public ConsoleDevice(final Console console) {
			this.console = console;
		}

		public TextDevice printf(String fmt, Object... params) throws ConsoleException {
			try {
				console.printf(fmt, params);
			} catch (final IllegalFormatException e) {
				throw new ConsoleException(e.getMessage());
			}
			return this;
		}

		public String readLine() throws ConsoleException {
			try {
				return console.readLine();
			} catch (final IOError e) {
				throw new ConsoleException(e.getMessage());
			}
		}

		public char[] readPassword() throws ConsoleException {
			try {
				return console.readPassword();
			} catch (final IOError e) {
				throw new ConsoleException(e.getMessage());
			}
		}

		public Reader reader() {
			return console.reader();
		}

		public PrintWriter writer() {
			return console.writer();
		}
	}

	private static TextDevice DEFAULT = (System.console() == null) ? streamDevice(System.in, System.out)
			: new ConsoleDevice(System.console());

	public static TextDevice defaultTextDevice() {
		return DEFAULT;
	}

	public static TextDevice streamDevice(final InputStream in, final OutputStream out) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		final PrintWriter writer = new PrintWriter(out, true);
		return new CharacterDevice(reader, writer);
	}
}
