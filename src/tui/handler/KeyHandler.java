package tui.handler;

import tui.event.KeyEvent;

@FunctionalInterface
public interface KeyHandler {
    void onKey(KeyEvent event);
}

