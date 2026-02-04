package tui.handler;

import tui.event.*;
import tui.input.TerminalEvent;

public final class EventDispatcher {

    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;

    public EventDispatcher(
        KeyHandler keyHandler,
        MouseHandler mouseHandler
    ) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
    }

    public void dispatch(TerminalEvent event)
	{
        if (event instanceof KeyEvent key)
		{
            keyHandler.onKey(key);
        } 
		else if (event instanceof MouseEvent mouse)
		{
            mouseHandler.onMouse(mouse);
        }
    }
}

