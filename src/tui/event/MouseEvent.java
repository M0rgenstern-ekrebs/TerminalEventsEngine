package tui.event;

import tui.input.TerminalEvent;
import java.util.EnumSet;

public record MouseEvent(
	EventType eventType,
	long timestamp,
    MouseButton button,
    Type type,
    int x,
    int y,
    EnumSet<Modifier> modifiers
) extends Event, implements TerminalEvent {

    public enum Type {
        PRESSED,
        RELEASED,
        DRAGGED,
        MOVED,
        SCROLLED
    }

    public enum Modifier {
        SHIFT, ALT, CTRL
    }
}

