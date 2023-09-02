
public class Token 
{
	private enum TokenType {WORD, NUMBER, SEPERATOR}
	private TokenType type;
	private String value;
	private int lineNumber;
	private int charPosition;
	
	// Takes in the character that indicates which token type it is but does not store it.
	public Token(char type, int lineNumber, int charPosition)
	{
		// Sets value to null for ease of ToString output.
		value = null;
		this.lineNumber = lineNumber;
		this.charPosition = charPosition;
		// Placeholder until more tokens get added in part 2
		if (type == '\n')
			this.type = TokenType.SEPERATOR; 
		
	}
	
	// Takes in a value to store and identifies if it is a word or number.
	public Token(String value, int lineNumber, int charPosition)
	{
		this.value = value;
		this.lineNumber = lineNumber;
		this.charPosition = charPosition;
		// Checks first letter to identify if value is word
		if (Character.isAlphabetic(value.charAt(0)) || value.charAt(0) == '_')
			type = TokenType.WORD;
		// Considers value a number if it is not a word
		else
			type = TokenType.NUMBER;
	}
	
	public String ToStringValue()
	{
		if (value != null)
			return new String(type + "(" + value + ")");
		else
			return type.toString();
	}
	
	public String ToStringPosition()
	{
		return new String("Line Number: " + lineNumber + " Character Position: " + charPosition);
	}
}
