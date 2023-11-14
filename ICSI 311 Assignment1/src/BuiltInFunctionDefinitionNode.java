import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode
{
	// When there are multiple parameters I use InterpreterArrayDataType in place of InterpreterDataType and pull the HashMap from that one since it would be the same.
	private Function<InterpreterDataType,String> function;
	private boolean isVariadic;
	
	public BuiltInFunctionDefinitionNode(boolean isVaridic, Function<InterpreterDataType,String> function) 
	{
		this.function = function;
		this.isVariadic = isVaridic;
	}
	
	public String execute(InterpreterDataType params)
	{
		return function.apply(params);
	}
	
	public boolean isVariadic()
	{
		return isVariadic;
	}

}
