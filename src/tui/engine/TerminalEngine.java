package tui.engine;

import tui.event.KeyEvent;
import tui.handler.DefaultHandlers;
import tui.handler.EventDispatcher;
import tui.input.TerminalEvent;

	//engine loop
	// while (running) {
    // inputPump.poll();

    // TerminalEvent ev;
    // while ((ev = queue.poll()) != null) {
    //     dispatcher.dispatch(ev);
    // }

    // renderer.render();
    // timer.sleep();
	// }

public final class TerminalEngine {

    private final InputPump input;
    private final EventQueue queue;
	private final EventDispatcher dispatcher;
	private Boolean isEventdriven = false;
	private Boolean running = false;

    public TerminalEngine(InputPump input, EventQueue queue, EventDispatcher dispatcher)
	{
        this.input = input;
        this.queue = queue;
		this.dispatcher = dispatcher;
    }

	public void run()
	{
		this.running = true;
		while(running)
		{
			if (isEventdriven == true)
			{
				processEventDriven();
			}
			else // is frame driven
			{
				processFrameBased();
			}
		}
	}

    public void pollInput() throws Exception {
        input.poll();
    }

    public TerminalEvent pollEvent() {
        return queue.poll();
    }

	private void processEventDriven() throws InterruptedException
	{
		Event event = EventQueue.take(); //blocks until event
		dispatcher.dispatch(event);
		// minimal update/reder only if needed
	}

	private final long TARGET_TIME_FRAME_NS = 1_000_000_000L / 60; // =60FPS
	private long lastFrameTime = System.nanoTime();
	private long accumulator = 0;

	private void processFrameBased()
	{
		long now = System.nanoTime();
		long deltaNs = now - lastFrameTime;
		lastFrameTime = now;

		accumulator += deltaNs;
		//process ALL pending events
		Event event;
		while((event = eventQueue.poll()) != null)
		{
			dispatcher.dispatch(event);
		}

		//fixed timestep update
		while(accumulator >= TARGET_TIME_FRAME_NS)
		{
			update(TARGET_TIME_FRAME_NS);
			accumulator -= TARGET_TIME_FRAME_NS;
		}

		render(accumulator / (double) TARGET_TIME_FRAME_NS); // interpolate if you want smooth visuals

		long frameBudget = TARGET_TIME_FRAME_NS - accumulator;

		if(frameBudget > 0)
		{
			try
			{
				Thread.sleep(Math.max(0, (frameBudget / 1_000_000L)));
			}
			catch (InterruptedException ingored) 
			{}
		}
	}

	public void setEventDriven(Boolean value)
	{
		this.isEventdriven = value;
	}

	public Boolean isEventDriven()
	{
		return (isEventdriven);
	}
}


