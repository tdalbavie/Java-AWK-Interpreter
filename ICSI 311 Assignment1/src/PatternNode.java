
public class PatternNode extends Node
{
	String pattern;
	
	public PatternNode(String pattern)
	{
		this.pattern = pattern;
	}
	
	public String getPattern()
	{
		return pattern;
	}
	
	// Does the same as getPattern but implemented for simplicity sake and readability.
	public String toString()
	{
		return pattern;
	}
}
