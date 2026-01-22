import java.io.IOException;
import java.util.EnumSet;
import static lib_ekrebs.defines.ECMA_48.ECMA_SGR_codes.*;

import org.jline.terminal.*;
import org.jline.utils.NonBlockingReader;

public class TerminalEventsHandler {
	private static final char	ESC   		= '\u001B';
	private static final String PASTE_START = ESC+"[200~";
	private static final String PASTE_END   = ESC+"[201~";
	private static final String CSI   		= ESC+"[";

	private static Terminal terminal;
	private static NonBlockingReader reader;

	public TerminalEventsHandler() throws IOException
	{
			terminal = TerminalBuilder.builder().system(true).build();
			if (terminal == null)
				throw new IOException("terminal failed to build");
			reader = terminal.reader();
	}

    public void run() throws IOException
	{
		try
		{
			terminal.enterRawMode();
			if (!terminal.trackMouse(Terminal.MouseTracking.Any))
				throw new IOException("terminal failed to enable Mouse tracking");
			terminal.writer().print("\033[?2004l"); //disable pasting if possible
			terminal.writer().flush();
			handleTerminalEvents(terminal, reader);
		}
		finally
		{
			terminal.trackMouse(Terminal.MouseTracking.Off);
			terminal.writer().print("\033[?2004h");
			terminal.writer().flush();
			terminal.reader().close();
			terminal.close();
		}
    }

	static void handleTerminalEvents(Terminal terminal, NonBlockingReader reader) throws IOException
	{
			int next = 0;
			Boolean reading;
			StringBuilder csi_buffer;

			System.out.println("Keyboard + mouse demo (press 'q' to quit)");
			System.out.println("Detects:\n");
			csi_buffer = new StringBuilder();
			reading = true;
			while (reading)
			{
				next = reader.read();
				if (next == -1)
					continue;
				else if (next == 'q')
				{
					printKeyEvent(next);
					reading = false;
				}
				else if (next == ESC) // ESC
				{
					handleESC(terminal, reader, csi_buffer);
					continue;
				}
				else // char is key
					printKeyEvent(next);
			}
	}

	static void handleESC(Terminal terminal, NonBlockingReader reader, StringBuilder csi_buffer) throws IOException
	{
			//ESC
			int next = 0;
			next = reader.read();
			if (next == -1)
				return;
			if (next == '[') // ESC + [ = CSI
			{
				handleCSI(terminal, reader, csi_buffer);
				return;
			}
			else // ESC + char = Alt key
			{
				printkeyAltEvent(next);
				return;
			}
	}

	static void handleCSI(Terminal terminal, NonBlockingReader reader, StringBuilder csi) throws IOException
	{
		int next = 0;
		
		csi.setLength(0);
		while (true)
		{
			next = reader.read();
			if (next == -1)
				return;
			csi.append((char) next);
			if (csi.length() == 1 && next >= 'A' && next <= 'D') // CSI + [A-D] = ArrowKey
			{
				printArrowkeyEvent(next);
				return;
			}
			else if (csi.charAt(0) == '<') // CSI + < = SGR mouse
			{
				handleSGR(terminal, reader);
				return;
			}
			else if (csi.charAt(0) == 'M') // CSI + M = X10/Legacy mouse
			{
				handleX10(terminal, reader);
				return;
			}
			else if (next == '~' && csi.toString().equals(PASTE_START)) // CSI + 200~ = Pasting
			{
				skipPaste(reader);
				return;
			}
			else if (Character.isLetter(next) || next == '~') // Unknown sequence ?
			{
				System.out.println("Unknown CSI: ESC[" + csi);
				return;
			}
		}
	}

	static void handleSGR(Terminal terminal, NonBlockingReader reader) throws IOException
	{
		MouseEvent me = terminal.readMouseEvent();
		if (me != null)
			printMouseEvent("SGR", me);
	}

	static void handleX10(Terminal terminal, NonBlockingReader reader) throws IOException
	{
		MouseEvent me = terminal.readMouseEvent();
		if (me != null)
			printMouseEvent("X10/Legacy", me);
	}

	static void skipPaste(NonBlockingReader reader) throws IOException
	{
		StringBuilder buf = new StringBuilder();
		while (true)
		{
			int ch = reader.read();
			if (ch == -1)
				return;
			buf.append((char) ch);
			if (buf.length() > PASTE_END.length())
				buf.delete(0, buf.length() - PASTE_END.length());
			if (buf.toString().endsWith(PASTE_END))
				return;
		}
	}

	static void printkeyAltEvent(int c)
	{
		System.out.print("\033[2K \r\tAlt+" + (char) c);
	}

	static void printArrowkeyEvent(int c)
	{
		String key;
		key = switch (c)
		{
			case 'A' -> "UP";
			case 'B' -> "DOWN";
			case 'C' -> "RIGHT";
			case 'D' -> "LEFT";
			default  -> "ERROR";
		};
		System.out.print("\033[2K \r\tKey Arrow: '" +YELLOW+ key +RESET+ "'");
	}


	static void printKeyEvent(int c)
	{
		switch (c)
		{
			case 9   -> System.out.print("\033[2K \r\tKey TAB");
			case 27  -> System.out.print("\033[2K \r\tKey ESC");
			case 127 -> System.out.print("\033[2K \r\tKey BACKSPACE");
			case 13  -> System.out.print("\033[2K \r\tKey ENTER");
			case 'q' -> System.out.print("\n\tquit");
			default  -> 
			{
				if (c >= 32 && c <= 126)
				{
					System.out.println("\033[2K \r\tKey CHAR: '" +YELLOW+ (char) c +RESET+ "'");
				}
				else if (c >= 1 && c <= 26)
				{
					char ctrl = (char) ('A' + c - 1);
					System.out.println("\033[2K \r\tKey "+YELLOW+"CTRL"+RESET+"+" + ctrl);
				}
				else		
					System.out.println("\033[2K \r\tKey CODE: " +MAGENTA+ c +RESET);
			}
		}
		return;
	}

	static void printMouseEvent(String encoding, MouseEvent me)
	{
		String MouseButton;

		MouseButton = switch (""+me.getButton())
		{
			case "Button1"   ->  "LeftClick";
			case "Button2"   ->  "MiddleClick";
			case "Button3"   ->  "RightClick";
			default  -> ""+me.getButton();
		};
		System.out.printf("\033[2K \r\t"+"%sMouse  ("+YELLOW+"%10s  "+BLUE+"%-10s"+RESET+")  "+"("+RED+"%3d"+RESET+","+GREEN+"%3d"+RESET+")",
			encoding,
			""+me.getType(),
			MouseButton,
			me.getX(),
			me.getY() 
		);
	}
}
