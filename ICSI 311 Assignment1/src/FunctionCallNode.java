import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode
{
	String name;
	Optional<LinkedList<Node>> parameters;
	
	// Called when no parameters are found.
	public FunctionCallNode(String name)
	{
		this.name = name;
		parameters = Optional.empty();
	}
	
	// Called when parameters are found.
	public FunctionCallNode(String name, LinkedList<Node> parameters)
	{
		this.name = name;
		this.parameters = Optional.of(parameters);
	}
	
	public String getName()
	{
		return name;
	}
	
	public Optional<LinkedList<Node>> getParameters()
	{
		return parameters;
	}
}
