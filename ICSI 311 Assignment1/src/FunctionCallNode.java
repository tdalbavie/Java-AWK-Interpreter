import java.util.LinkedList;

public class FunctionCallNode extends StatementNode
{
	private String FunctionName;
	private LinkedList<Node> Parameters;
	
	// Called when parameters are found.
	public FunctionCallNode(String FunctionName, LinkedList<Node> Parameters)
	{
		this.FunctionName = FunctionName;
		this.Parameters = new LinkedList<Node>(Parameters);
	}
	
	public String getName()
	{
		return FunctionName;
	}
	
	public LinkedList<Node> getParameters()
	{
		return Parameters;
	}
	
	public String toString()
	{
		String contents = "Function name: " + FunctionName + "\n";
		if (Parameters.isEmpty() == false)
		{
			contents += "Parameter name(s): " + Parameters.toString();
		}
		return contents;
	}
}
