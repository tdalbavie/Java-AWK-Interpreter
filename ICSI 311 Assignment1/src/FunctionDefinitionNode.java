import java.util.LinkedList;

public class FunctionDefinitionNode extends Node 
{
	private String FunctionName;
	private LinkedList<String> ParameterNames;
	private LinkedList<StatementNode> Statements;
	
	public FunctionDefinitionNode(LinkedList<StatementNode> Statements, LinkedList<String> ParameterNames, String FunctionName)
	{
		this.FunctionName = FunctionName;
		this.ParameterNames = new LinkedList<String>(ParameterNames);
		this.Statements = new LinkedList<StatementNode>(Statements);
	}
	
	public String FunctionNameAccessor()
	{
		return FunctionName;
	}
	
	public LinkedList<String> ParameterNamesAccessor()
	{
		return ParameterNames;
	}
	
	public LinkedList<StatementNode> StatementsAccessor()
	{
		return Statements;
	}
	
	// No way to output Statements since it has not been implemented yet.
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
