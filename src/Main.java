import java.io.IOException;

public class Main
{
	public static void main(String[] args)
	{
		TerminalEventsHandler teH;

		try
		{
			teH = new TerminalEventsHandler();
			teH.run();
		}
		catch (IOException e)
		{
			System.err.println("error: "+e.getMessage());
		}
	}
}
