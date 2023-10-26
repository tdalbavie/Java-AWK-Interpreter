import java.util.LinkedList;

public class FunctionDefinitionNode extends Node 
{
	private String FunctionName;
	private LinkedList<String> ParameterNames;
	private LinkedList<StatementNode> Statements;
	
	// Empty constructor so that BuiltInFunctionDefinitionNode does not need to setup this class.
	public FunctionDefinitionNode()
	{
		
	}
	
	public FunctionDefinitionNode(LinkedList<StatementNode> Statements, LinkedList<String> ParameterNames, String FunctionName)
	{
		this.FunctionName = FunctionName;
		this.ParameterNames = new LinkedList<String>(ParameterNames);
		this.Statements = new LinkedList<StatementNode>(Statements);
	}
	
	public String getFunctionName()
	{
		return FunctionName;
	}
	
	public LinkedList<String> getParameterNames()
	{
		return ParameterNames;
	}
	
	public LinkedList<StatementNode> getStatements()
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
