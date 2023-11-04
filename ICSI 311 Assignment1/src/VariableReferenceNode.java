import java.util.Optional;

public class VariableReferenceNode extends Node
{
	private String name;
	private Optional<Node> index;
	
	
	public VariableReferenceNode(String name)
	{
		this.name = name;
		index = Optional.empty();
	}
	
	// For when an expression is present.
	public VariableReferenceNode(String name, Optional<Node> index)
	{
		this(name);
		this.index = index;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Optional<Node> getIndex()
	{
		return index;
	}
	
	public String toString()
	{
		String contents = "Name: " + name + "\n";
		
		// If present, prints expression once node type is identified.
		if (index.isPresent())
		{
			if (index.get() instanceof OperationNode)
			{
				OperationNode opNode = (OperationNode) index.get();
				opNode.toString();
			}
			
			else if (index.get() instanceof VariableReferenceNode)
			{
				VariableReferenceNode vrNode = (VariableReferenceNode) index.get();
				vrNode.toString();
			}
			
			else if (index.get() instanceof ConstantNode)
			{
				ConstantNode constNode = (ConstantNode) index.get();
				constNode.toString();
			}
			
			else if (index.get() instanceof PatternNode)
			{
				PatternNode patNode = (PatternNode) index.get();
				patNode.toString();
			}
			
			else
			{
				contents += "Unknown type\n";
			}
		}
		
		return contents;
	}
}
