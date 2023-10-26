import java.util.HashMap;

public class InterpreterArrayDataType extends InterpreterDataType
{
	// LinkedHashMap???
	HashMap<String, InterpreterDataType> arrayType;
	
	public InterpreterArrayDataType()
	{
		arrayType = new HashMap<String, InterpreterDataType>();
	}
	
	public HashMap<String, InterpreterDataType> getArrayType()
	{
		return arrayType;
	}
}
