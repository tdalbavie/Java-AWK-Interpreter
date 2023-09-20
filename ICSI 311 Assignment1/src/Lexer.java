import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;

public class Lexer 
{
	private StringHandler sh;
	private int lineNumber;
	private int charPosition;
	private HashMap<String, Token.TokenType> types;
	private HashMap<String, Token.TokenType> singleSymbolTypes;
	private HashMap<String, Token.TokenType> doubleSymbolTypes;
	
	
	public Lexer(String fileContents)
	{
		sh = new StringHandler(fileContents);
		lineNumber = 1;
		charPosition = 1;
		HashMapPopulator();
	}
	
	public LinkedList<Token> Lex()
	{
		// Holds length of string to make sure there is still data in StringHandler
		int stringLength = sh.Remainder().length() - 1;
		LinkedList<Token> tokens = new LinkedList<Token>();
		
		// Makes sure that file is not already empty
		if (stringLength >= 0)
		{
			// Loops until no more characters exist.
			while (sh.IsDone() == false)
			{
				// Skips tab or space and moves to the next line
				if (sh.Peek(0) == ' ' || sh.Peek(0) == '\t')
				{
					// Makes sure there is another character after whitespace otherwise exits loop
					if(sh.Remainder().length() - 1 > 0)
					{
						sh.Swallow(1);
						charPosition++;
					}
					else
						break;
				}
				
				// Creates a separator token and moves to the next line.
				else if (sh.Peek(0) == '\n')
				{
					tokens.add(new Token(Token.TokenType.SEPARATOR, lineNumber, charPosition));
					sh.Swallow(1);
					lineNumber++;
					charPosition = 1;

				}
				
				// Ignores carriage return like a whitespace.
				else if(sh.Peek(0) == '\r')
				{
					// Checks if return carriage is the last character in the string.
					if(sh.Remainder().length() - 1 > 0)
					{
						sh.Swallow(1);
						charPosition++;
					}
					else
						break;
				}
				
				// Identifies the start of a word then loops until the full word is found and creates a token.
				else if (Character.isAlphabetic(sh.Peek(0)) || sh.Peek(0) == '_')
				{
					tokens.add(ProcessWord());
				}
				
				// Identifies the start of a number and loops until the full number is found.
				else if (Character.isDigit(sh.Peek(0)))
				{
					tokens.add(ProcessNumber());
				}
				
				// Identifies the start of a decimal number until the full number is found.
				else if (sh.Peek(0) == '.')
				{
					tokens.add(ProcessNumber());
				}
				
				else if (sh.Peek(0) == '#')
				{
					// Checks if the comment takes up the whole line.
					if (charPosition == 0)
					{
						while (sh.Peek(0) != '\n')
							sh.GetChar();
						
						sh.Swallow(1);
						lineNumber++;
						charPosition = 1;
					}
					// Creates separator token if it is at the end of a line.
					else
					{
						while (sh.Peek(0) != '\n')
							sh.GetChar();

						tokens.add(new Token(Token.TokenType.SEPARATOR, lineNumber, charPosition));
						sh.Swallow(1);
						lineNumber++;
						charPosition = 1;
					}
				}
				
				// Checks for the start of a string.
				else if (sh.Peek(0) == '"')
				{
					int charPositionCount = charPosition;
					String literal = sh.HandleStringLiteral();
					// Sets character positions string length + 2 to make up for the two " positions.
					charPosition += literal.length() + 2;
					tokens.add(new Token(literal, Token.TokenType.STRINGLITERAL, lineNumber, charPositionCount));
				}
				
				// Checks for the start of a pattern.
				else if (sh.Peek(0) == '`')
				{
					int charPositionCount = charPosition;
					String pattern = sh.HandlePattern();
					// Does the same as StringLiteral
					charPosition += pattern.length() + 2;
					tokens.add(new Token(pattern, Token.TokenType.PATTERN, lineNumber, charPositionCount));
				}
				
				// Checks if character is in symbol library
				else if (singleSymbolTypes.containsKey(sh.PeekString(1)))
				{
					tokens.add(ProcessSymbol());
				}
				
				else if (doubleSymbolTypes.containsKey(sh.PeekString(2)))
				{
					tokens.add(ProcessSymbol());
				}
				
				// Returns an error for unknown character and stops program.
				else
				{
					throw new InputMismatchException("Character not valid: " + sh.Peek(0));
				}
			}
		}
		
		// Adds a separator token to the end of the token list.
		tokens.add(new Token(Token.TokenType.SEPARATOR, lineNumber, charPosition));
		
		return tokens;
	}
	
	// Peeks at following characters until full word is found.
	private Token ProcessWord()
	{
		// Holds the word to be added to the token
		String word = "";
		// Holds first character position of the number
		int charPositionCount = charPosition;
		
		// Loops until it finds a character that does not match awk word syntax.
		while(Character.isAlphabetic(sh.Peek(0)) || Character.isDigit(sh.Peek(0)) || sh.Peek(0) == '_')
		{
			word += sh.GetChar();
			charPosition++;
		}
		
		if (types.containsKey(word))
			return new Token(types.get(word), lineNumber, charPositionCount);
		else
			return new Token(word, Token.TokenType.WORD, lineNumber, charPositionCount);
	}
	
	// Peeks at following characters until full number is found.
	private Token ProcessNumber()
	{
		// Holds the number to be added to the token
		String number = "";
		boolean foundPoint = false;
		// Holds first character position of the number
		int charPositionCount = charPosition;
		
		// Loops until it finds a character that does not match awk number syntax.
		while(Character.isDigit(sh.Peek(0)) || sh.Peek(0) == '.')
		{
			// Throws exception if foundPoint is already true.
			if (foundPoint == true && sh.Peek(0) == '.')
				throw new IllegalArgumentException("Number contains more than one decimal point");
			
			// Sets foundPoint to true 
			else if (foundPoint == false && sh.Peek(0) == '.')
			{
				foundPoint = true;
				number += sh.GetChar();
				charPosition++;
			}
			else
			{
				number += sh.GetChar();
				charPosition++;
			}
		}
			
		return new Token(number, Token.TokenType.NUMBER, lineNumber, charPositionCount);
	}
	
	private Token ProcessSymbol()
	{
		String symbol = "";
		// Checks if symbol is double or single
		if (doubleSymbolTypes.containsKey(sh.PeekString(2)))
		{
			int charPositionCount = charPosition;
			symbol += sh.PeekString(2);
			sh.Swallow(2);
			charPosition += 2;
			return new Token(doubleSymbolTypes.get(symbol), lineNumber, charPositionCount);
		}
		else if (singleSymbolTypes.containsKey(sh.PeekString(1)))
		{
			int charPositionCount = charPosition;
			symbol += sh.GetChar();
			charPosition++;
			return new Token(singleSymbolTypes.get(symbol), lineNumber, charPositionCount);
		}
		else
			return null;
	}
	
	// Called by constructor to populate the HashMap
	private void HashMapPopulator()
	{
		types = new HashMap<String, Token.TokenType>();
		types.put("while", Token.TokenType.WHILE);
		types.put("if", Token.TokenType.IF);
		types.put("do", Token.TokenType.DO);
		types.put("for", Token.TokenType.FOR);
		types.put("break", Token.TokenType.BREAK);
		types.put("continue", Token.TokenType.CONTINUE);
		types.put("else", Token.TokenType.ELSE);
		types.put("return", Token.TokenType.RETURN);
		types.put("BEGIN", Token.TokenType.BEGIN);
		types.put("END", Token.TokenType.END);
		types.put("print", Token.TokenType.PRINT);
		types.put("printf", Token.TokenType.PRINTF);
		types.put("next", Token.TokenType.NEXT);
		types.put("in", Token.TokenType.IN);
		types.put("delete", Token.TokenType.DELETE);
		types.put("getline", Token.TokenType.GETLINE);
		types.put("exit", Token.TokenType.EXIT);
		types.put("nextfile", Token.TokenType.NEXTFILE);
		types.put("function", Token.TokenType.FUNCTION);
		types.put("'", Token.TokenType.PATTERN);
		
		singleSymbolTypes = new HashMap<String, Token.TokenType>();
		singleSymbolTypes.put("{", Token.TokenType.OPENCURLBRACK);
		singleSymbolTypes.put("}", Token.TokenType.CLOSECURLBRACK);
		singleSymbolTypes.put("[", Token.TokenType.OPENBRACK);
		singleSymbolTypes.put("]", Token.TokenType.CLOSEBRACK);
		singleSymbolTypes.put("(", Token.TokenType.OPENPAREN);
		singleSymbolTypes.put(")", Token.TokenType.CLOSEPAREN);
		singleSymbolTypes.put("$", Token.TokenType.DOLLAR);
		singleSymbolTypes.put("~", Token.TokenType.MATCH);
		singleSymbolTypes.put("=", Token.TokenType.ASSIGN);
		singleSymbolTypes.put("<", Token.TokenType.LESSTHAN);
		singleSymbolTypes.put(">", Token.TokenType.GREATERTHAN);
		singleSymbolTypes.put("!", Token.TokenType.NOT);
		singleSymbolTypes.put("+", Token.TokenType.PLUS);
		singleSymbolTypes.put("^", Token.TokenType.EXPONENT);
		singleSymbolTypes.put("-", Token.TokenType.MINUS);
		singleSymbolTypes.put("?", Token.TokenType.QUESTIONMARK);
		singleSymbolTypes.put(":", Token.TokenType.COLON);
		singleSymbolTypes.put("*", Token.TokenType.MULTIPLY);
		singleSymbolTypes.put("/", Token.TokenType.DIVIDE);
		singleSymbolTypes.put("%", Token.TokenType.MODULO);
		singleSymbolTypes.put(";", Token.TokenType.SEPARATOR);
		singleSymbolTypes.put("\n", Token.TokenType.SEPARATOR);
		singleSymbolTypes.put("|", Token.TokenType.BAR);
		singleSymbolTypes.put(",", Token.TokenType.COMMA);
		
		doubleSymbolTypes = new HashMap<String, Token.TokenType>();
		doubleSymbolTypes.put(">=", Token.TokenType.GREATEROREQUAL);
		doubleSymbolTypes.put("++", Token.TokenType.INCREMENT);
		doubleSymbolTypes.put("--", Token.TokenType.DECREMENT);
		doubleSymbolTypes.put("<=", Token.TokenType.LESSOREQUAL);
		doubleSymbolTypes.put("==", Token.TokenType.EQUALS);
		doubleSymbolTypes.put("!=", Token.TokenType.NOTEQUALS);
		doubleSymbolTypes.put("^=", Token.TokenType.EXPONENTEQUALS);
		doubleSymbolTypes.put("%=", Token.TokenType.MODEQUALS);
		doubleSymbolTypes.put("*=", Token.TokenType.MULTIPLYEQUALS);
		doubleSymbolTypes.put("/=", Token.TokenType.DIVIDEEQUALS);
		doubleSymbolTypes.put("+=", Token.TokenType.PLUSEQUALS);
		doubleSymbolTypes.put("-=", Token.TokenType.MINUSEQUALS);
		doubleSymbolTypes.put("!~", Token.TokenType.NOTMATCH);
		doubleSymbolTypes.put("&&", Token.TokenType.AND);
		doubleSymbolTypes.put(">>", Token.TokenType.APPEND);
		doubleSymbolTypes.put("||", Token.TokenType.OR);
	}
}
