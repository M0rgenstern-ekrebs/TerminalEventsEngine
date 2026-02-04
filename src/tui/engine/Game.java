package tui.engine;

import tui.input.TerminalEvent;
import tui.render.*;
import tui.handler.EventDispatcher;
import tui.handler.DefaultHandlers;
import tui.scene.SceneManager;
import tui.input.EventQueue;
import tui.input.InputPump;

public final class Game {

    private boolean running = true;

    private final TerminalEngine	terminal;
	private final EventDispatcher	dispatcher;
    private final SceneManager		scenes;
    private final Renderer			renderer;
    private final FrameTimer		timer;

    public Game()
	{
        this.terminal = new TerminalEngine(new InputPump(null), new EventQueue());
		this.dispatcher = new EventDispatcher(DefaultHandlers.KEY_DEBUG, DefaultHandlers.MOUSE_DEBUG);
        this.renderer = new Renderer();
        this.scenes = new SceneManager();
        this.timer = new FrameTimer();
    }

	public Game(TerminalEngine terminal, EventDispatcher dispatcher, Renderer renderer, SceneManager scenes, FrameTimer timer)
	{
		this.terminal = terminal;
		this.dispatcher = dispatcher;
		this.renderer = renderer;
		this.scenes = scenes;
		this.timer = timer;
	}

    public void run() throws Exception
	{
        while (running)
		{
            // 1️⃣ Input
            terminal.pollInput();

            TerminalEvent ev;
            while ((ev = terminal.pollEvent()) != null)
			{
				dispatcher.dispatch(ev);
                scenes.handle(ev);
            }

            // 2️⃣ Update
            scenes.update();

            // 3️⃣ Render
            renderer.render(scenes.current());

            // 4️⃣ Timing
            timer.sleep();
        }
    }

    public void stop()
	{
        running = false;
    }
}

