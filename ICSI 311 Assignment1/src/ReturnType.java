
public class ReturnType 
{
	public enum ReturnTypes{NORMAL, BREAK, CONTINUE, RETURN}
	private ReturnTypes typeReturned;
	private String returnValue;
	
	public ReturnType(ReturnTypes typeReturned)
	{
		this.typeReturned = typeReturned;
	}
	
	public ReturnType(ReturnTypes typeReturned, String returnValue)
	{
		this(typeReturned);
		this.returnValue = returnValue;
	}
	
	public ReturnTypes getTypeReturned()
	{
		return typeReturned;
	}
	
	public String getReturnValue()
	{
		return returnValue;
	}
	
	
}
