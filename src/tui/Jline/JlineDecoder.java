package tui.Jline;

package tui.Jline;

import org.jline.terminal.Terminal;
import org.jline.utils.NonBlockingReader;
import tui.input.EventQueue;
import tui.input.TerminalEvent;
import tui.event.*;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

public final class JlineDecoder {

    private static final char ESC = '\u001B';
    private static final String PASTE_START = ESC + "[200~";
    private static final String PASTE_END   = ESC + "[201~";

    private final Terminal terminal;
    private final NonBlockingReader reader;
    private final EventQueue queue;

    private final StringBuilder csi = new StringBuilder();

	private static final Map<Character, KeyCode> CSI_KEY_TABLE = Map.of
	(
		'A', KeyCode.ARROW_UP,
		'B', KeyCode.ARROW_DOWN,
		'C', KeyCode.ARROW_RIGHT,
		'D', KeyCode.ARROW_LEFT,
		'H', KeyCode.HOME,
		'F', KeyCode.END
	);

	private static final Map<String, KeyCode> CSI_TILDE_TABLE = Map.of
	(
		"1~", KeyCode.HOME,
		"2~", KeyCode.INSERT,
		"3~", KeyCode.DELETE,
		"4~", KeyCode.END,
		"5~", KeyCode.PAGE_UP,
		"6~", KeyCode.PAGE_DOWN
	);



    public JlineDecoder(
        Terminal terminal,
        NonBlockingReader reader,
        EventQueue queue
    ) {
        this.terminal = terminal;
        this.reader = reader;
        this.queue = queue;
    }


	private void decodeCSI() throws IOException
	{
		csi.setLength(0);

		while (true) {
			int ch = reader.read();
			if (ch == -1) continue;

			char c = (char) ch;
			csi.append(c);

			// 1️⃣ Single-letter CSI
			KeyCode key = CSI_KEY_TABLE.get(c);
			if (key != null && csi.length() == 1) {
				queue.push(new KeyEvent(
					key,
					'\0',
					EnumSet.noneOf(KeyEvent.Modifier.class)
				));
				return;
			}

			// 2️⃣ Mouse encodings
			if (csi.charAt(0) == '<') {
				queue.push(decodeSGRMouse());
				return;
			}

			if (csi.charAt(0) == 'M') {
				queue.push(decodeX10Mouse());
				return;
			}

			// 3️⃣ Tilde-terminated sequences (e.g. 200~)
			if (c == '~') {
				handleTildeCSI(csi.toString());
				return;
			}

			// 4️⃣ Unknown CSI → stop
			if (Character.isLetter(c)) {
				return;
			}
		}
	}


	private void handleTildeCSI(String seq) {
		if ("200~".equals(seq)) {
			skipPaste();
			return;
		}

		KeyCode key = CSI_TILDE_TABLE.get(seq);
		if (key != null) {
			queue.push(new KeyEvent(
				key,
				'\0',
				EnumSet.noneOf(KeyEvent.Modifier.class)
			));
		}
	}




    public void poll() throws IOException {
        int ch = reader.read();
        if (ch == -1) return;

        if (ch == ESC) {
            decodeEscape();
        } else {
            queue.push(decodeKey(ch));
        }
    }

	private void decodeEscape() throws IOException {
        int next;
        while ((next = reader.read()) == -1) {}

        if (next == '[') {
            decodeCSI();
        } else {
            queue.push(KeyEvent.alt((char) next));
        }
    }

	// private void decodeCSI() throws IOException
	// {
    //     csi.setLength(0);

    //     while (true) {
    //         int ch = reader.read();
    //         if (ch == -1) continue;

    //         csi.append((char) ch);

    //         if (csi.length() == 1 && ch >= 'A' && ch <= 'D') {
    //             queue.push(KeyEvent.arrow(ch));
    //             return;
    //         }

    //         if (csi.charAt(0) == '<') {
    //             queue.push(decodeSGRMouse());
    //             return;
    //         }

    //         if (csi.charAt(0) == 'M') {
    //             queue.push(decodeX10Mouse());
    //             return;
    //         }

    //         if (ch == '~' && csi.toString().equals(PASTE_START)) {
    //             skipPaste();
    //             return;
    //         }

    //         if (Character.isLetter(ch) || ch == '~') {
    //             return; // unknown CSI
    //         }
    //     }
    // }

	private MouseEvent decodeX10Mouse() throws IOException
	{
        return terminal.readMouseEvent();
    }

	private MouseEvent decodeSGRMouse() throws IOException
	{
        // (your existing logic, but return MouseEvent only)
    }




}
