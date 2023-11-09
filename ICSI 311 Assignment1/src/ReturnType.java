import java.util.Optional;

public class ReturnType 
{
	public enum ReturnTypes{NORMAL, BREAK, CONTINUE, RETURN}
	private ReturnTypes typeReturned;
	private Optional<String> returnValue;
	
	public ReturnType(ReturnTypes typeReturned)
	{
		this.typeReturned = typeReturned;
		returnValue = Optional.empty(); // Defined as an empty string so the .isEmpty method can work for cases where return does not have a return value.
	}
	
	public ReturnType(ReturnTypes typeReturned, Optional<String> returnValue)
	{
		this.typeReturned = typeReturned;
		this.returnValue = returnValue;
	}
	
	public ReturnTypes getTypeReturned()
	{
		return typeReturned;
	}
	
	public Optional<String> getReturnValue()
	{
		return returnValue;
	}
	
	
}
