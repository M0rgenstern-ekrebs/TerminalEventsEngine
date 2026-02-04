package tui.event;

public enum KeyCode {

    // Printable
    CHARACTER,

    // Control keys
    ENTER,
    TAB,
    BACKSPACE,
    ESCAPE,

    // Navigation
    ARROW_UP,
    ARROW_DOWN,
    ARROW_LEFT,
    ARROW_RIGHT,
    HOME,
    END,
    PAGE_UP,
    PAGE_DOWN,
    INSERT,
    DELETE,

    // Function keys
    F1, F2, F3, F4, F5, F6,
    F7, F8, F9, F10, F11, F12,

    // Unknown / fallback
    UNKNOWN
}

