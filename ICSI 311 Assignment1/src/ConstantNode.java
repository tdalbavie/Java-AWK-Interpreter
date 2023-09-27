
public class ConstantNode extends Node
{
	String value;
	
	public ConstantNode(String stringLiteral)
	{
		this.value = stringLiteral;
	}
	
	public String getConstantValue()
	{
		return value;
	}
}
