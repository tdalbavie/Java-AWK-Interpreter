import java.util.HashMap;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode
{
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
