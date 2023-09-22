
public class Token 
{
	public enum TokenType {WORD, NUMBER, SEPARATOR, WHILE, 
		IF, DO, FOR, BREAK, CONTINUE, ELSE, RETURN, 
		BEGIN, END, PRINT, PRINTF, NEXT, IN, DELETE, 
		GETLINE, EXIT, NEXTFILE, FUNCTION, STRINGLITERAL,
		PATTERN, OPENCURLBRACK, CLOSECURLBRACK, OPENBRACK, 
		CLOSEBRACK, OPENPAREN, CLOSEPAREN, DOLLAR, MATCH, 
		ASSIGN,LESSTHAN, GREATERTHAN, NOT, PLUS, EXPONENT, 
		MINUS,QUESTIONMARK, COLON, MULTIPLY, DIVIDE, MODULO, 
		BAR, COMMA, GREATEROREQUAL, INCREMENT, DECREMENT, 
		LESSOREQUAL, EQUALS, NOTEQUALS, EXPONENTEQUALS, 
		MODEQUALS, MULTIPLYEQUALS, DIVIDEEQUALS, PLUSEQUALS, 
		MINUSEQUALS, NOTMATCH, AND, APPEND, OR}
	private TokenType type;
	private String value;
	private int lineNumber;
	private int charPosition;
	
	// Takes in token type and position of token.
	public Token(TokenType type, int lineNumber, int charPosition)
	{
		this.lineNumber = lineNumber;
		this.charPosition = charPosition;
		this.type = type; 
	}
	
	// Takes in first constructor but sets value for cases like abstract words or numbers.
	public Token(String value, TokenType type, int lineNumber, int charPosition)
	{
		this(type, lineNumber, charPosition);
		this.value = value;
	}
	
	public TokenType getType()
	{
		return type;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public int getCharPosition()
	{
		return charPosition;
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
