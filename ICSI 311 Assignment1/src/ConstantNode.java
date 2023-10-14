
public class ConstantNode extends Node
{
	private String value;
	
	public ConstantNode(String value)
	{
		this.value = value;
	}
	
	public String getConstantValue()
	{
		return value;
	}
	
	// Does the same as getConstantValue but implemented for simplicity sake and readability.
	public String toString()
	{
		return value;
	}
}
