import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode
{
	private String FunctionName;
	private LinkedList<Node> ParameterNames;
	
	// Called when parameters are found.
	public FunctionCallNode(String FunctionName, LinkedList<Node> ParameterNames)
	{
		this.FunctionName = FunctionName;
		this.ParameterNames = new LinkedList<Node>(ParameterNames);
	}
	
	public String getName()
	{
		return FunctionName;
	}
	
	public LinkedList<Node> getParameterNames()
	{
		return ParameterNames;
	}
	
	public String toString()
	{
		String contents = "Function name: " + FunctionName + "\n";
		if (ParameterNames.isEmpty() == false)
		{
			contents += "Parameter name(s): " + ParameterNames.toString();
		}
		return contents;
	}
}
