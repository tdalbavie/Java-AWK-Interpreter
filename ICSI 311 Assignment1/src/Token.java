
public class Token 
{
	private enum TokenType {WORD, NUMBER, SEPERATOR}
	private TokenType type;
	private String value;
	private int lineNumber;
	private int charPosition;
	
	public Token()
	{
		
	}
}
