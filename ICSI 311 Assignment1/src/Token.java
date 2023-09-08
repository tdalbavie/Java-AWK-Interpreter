
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
	
	// Takes in the character that indicates which token type it is but does not store it.
	public Token(TokenType type, int lineNumber, int charPosition)
	{
		// Sets value to null for ease of ToString output.
		value = null;
		this.lineNumber = lineNumber;
		this.charPosition = charPosition;
		this.type = type; 
		
	}
	
	// Takes in a value to store and identifies if it is a word or number.
	public Token(String value, TokenType type, int lineNumber, int charPosition)
	{
		this.value = value;
		this.lineNumber = lineNumber;
		this.charPosition = charPosition;
		this.type = type;
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
