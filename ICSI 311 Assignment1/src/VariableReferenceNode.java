import java.util.Optional;

public class VariableReferenceNode extends Node
{
	private String name;
	private Optional<Node> expression;
	
	
	public VariableReferenceNode(String name)
	{
		this.name = name;
	}
	
	public VariableReferenceNode(String name, Optional<Node> expression)
	{
		this(name);
		this.expression = expression;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Optional<Node> getExpression()
	{
		return expression;
	}
}
