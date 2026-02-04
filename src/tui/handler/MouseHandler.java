package tui.handler;

import tui.event.MouseEvent;

@FunctionalInterface
public interface MouseHandler {
    void onMouse(MouseEvent event);
}