package tui.input;

public abstract class Event
{

}

public enum EventType
{
	KEY_DOWN,
	KEY_UP,
	MOUSE_MOVE,
	WINDOW_RESIZE,
	CUSTOM,
	UNKNOWN,
}

public interface TerminalEvent
{
	EventType type();
	long timestamps();
}
