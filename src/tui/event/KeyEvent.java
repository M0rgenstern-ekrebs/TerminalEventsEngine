package tui.event;

import tui.input.TerminalEvent;
import java.util.EnumSet;

public record KeyEvent(
    KeyCode code,
    char character,
    EnumSet<Modifier> modifiers
) implements TerminalEvent {

    public enum Modifier {
        SHIFT, ALT, CTRL
    }

    public boolean isCharacter() {
        return code == KeyCode.CHARACTER;
    }
}

