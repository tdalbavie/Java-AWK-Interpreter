import java.util.HashMap;
import java.util.LinkedList;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode
{
	HashMap<String, InterpreterDataType> BuiltIns;
	
	public BuiltInFunctionDefinitionNode(LinkedList<StatementNode> Statements, LinkedList<String> ParameterNames, String FunctionName) 
	{
		super(Statements, ParameterNames, FunctionName);
	}
	
	

}
