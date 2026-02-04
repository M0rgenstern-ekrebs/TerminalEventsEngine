package tui.handler;

import tui.event.KeyEvent;
import tui.event.MouseEvent;

public final class DefaultHandlers
{

    private DefaultHandlers() {}

	public static final KeyHandler KEY_DEBUG = event -> {
        StringBuilder sb;
		
		sb = new StringBuilder();
        sb.append("[KEY] ");
        sb.append(event.code());
        if (event.isCharacter())
		{
            sb.append(" '").append(event.character()).append("'");
        }
        if (!event.modifiers().isEmpty())
		{
            sb.append(" mods=").append(event.modifiers());
        }
        System.out.println(sb);
    };

	public static final MouseHandler MOUSE_DEBUG = event -> {
        StringBuilder sb;
		
		sb = new StringBuilder();
        sb.append("[MOUSE] ");
        sb.append(event.type());
        sb.append(" ");
        sb.append(event.button());
        sb.append(" @(");
        sb.append(event.x()).append(",").append(event.y()).append(")");
        if (!event.modifiers().isEmpty())
		{
            sb.append(" mods=").append(event.modifiers());
        }
        System.out.println(sb);
    };
}

