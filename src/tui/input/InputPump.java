package tui.input;

import org.jline.terminal.*;
import tui.Jline.JlineDecoder;

public final class InputPump implements AutoCloseable {

    private final Terminal terminal;
    private final JlineDecoder decoder;

    public InputPump(EventQueue queue) throws Exception {
        terminal = TerminalBuilder.builder().system(true).build();
        terminal.enterRawMode();
        terminal.trackMouse(Terminal.MouseTracking.Any);

        decoder = new JlineDecoder(
            terminal,
            terminal.reader(),
            queue
        );
    }

    public void poll() throws Exception {
        decoder.poll();
    }

    @Override
    public void close() throws Exception {
        terminal.trackMouse(Terminal.MouseTracking.Off);
        terminal.close();
    }
}

