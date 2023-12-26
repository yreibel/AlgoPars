package algopars;

public class TraceExec
{
	private final String  trace;
	private final boolean entreeClavier;
	
	public TraceExec(String trace, boolean entreeClavier)
	{
		this.trace = trace;
		this.entreeClavier = entreeClavier;
	}
	
	public boolean estEntreeClavier() { return this.entreeClavier; }
	
	public String toString() { return this.trace; }
}
