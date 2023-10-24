import java.util.HashMap;
import java.util.List;

public class Interpreter 
{
	class LineManager
	{
		List<String> input;
		
		public LineManager(List<String> input)
		{
			this.input = input;
		}
		
		public boolean SplitAndAssign()
		{
			return false;
		}
	}
	
	HashMap<String, InterpreterDataType> globalVariables;
	HashMap<String, FunctionDefinitionNode> functionCalls;
	
	public Interpreter(ProgramNode program, String filePath)
	{
		
	}
}
