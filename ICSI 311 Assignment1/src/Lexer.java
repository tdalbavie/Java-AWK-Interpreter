import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;

public class Lexer 
{
	private StringHandler sh;
	private int lineNumber;
	private int charPosition;
	private HashMap<String, Token.TokenType> types;
	
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
					Token linefeed = new Token(Token.TokenType.SEPERATOR, lineNumber, charPosition);
					tokens.add(linefeed);
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
					Token word = ProcessWord();
					tokens.add(word);
				}
				
				// Identifies the start of a number and loops until the full number is found.
				else if (Character.isDigit(sh.Peek(0)))
				{
					Token number = ProcessNumber();
					tokens.add(number);
				}
				
				// Identifies the start of a decimal number until the full number is found.
				else if (sh.Peek(0) == '.')
				{
					Token decimal = ProcessNumber();
					tokens.add(decimal);
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
						
						Token linefeed = new Token(Token.TokenType.SEPERATOR, lineNumber, charPosition);
						tokens.add(linefeed);
						sh.Swallow(1);
						lineNumber++;
						charPosition = 1;
					}
				}
				
				else if (sh.Peek(0) == '"')
				{
					String literal = sh.HandleStringLiteral();
					// Sets character positions string length + 2 to make up for the two " positions.
					charPosition += literal.length() + 2;
					Token stringLiteral = new Token(literal, Token.TokenType.STRINGLITERAL, lineNumber, charPosition);
					tokens.add(stringLiteral);
				}
				
				// Returns an error for unknown character and stops program.
				else
				{
					throw new InputMismatchException("Character not valid");
				}
			}
		}
		
		return tokens;
	}
	
	// Peeks at following characters until full word is found.
	public Token ProcessWord()
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
		
		// Throws an exception if there is an invalid character still attached to the word.
		if (Character.isWhitespace(sh.Peek(0)) == false && sh.Peek(0) != '\r' && sh.Peek(0) != '\n' && sh.Peek(0) != '\0')
			throw new IllegalArgumentException("Words contain invalid character(s)");
		
		if (types.containsKey(word))
			return new Token(types.get(word), lineNumber, charPositionCount);
		else
			return new Token(word, Token.TokenType.WORD, lineNumber, charPositionCount);
	}
	
	// Peeks at following characters until full number is found.
	public Token ProcessNumber()
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
		
		// Throws an exception if there is an invalid character still attached to the number.
		if (Character.isWhitespace(sh.Peek(0)) == false && sh.Peek(0) != '\r' && sh.Peek(0) != '\n' && sh.Peek(0) != '\0')
			throw new IllegalArgumentException("Numbers contains invalid character(s)");
			
		return new Token(number, Token.TokenType.NUMBER, lineNumber, charPositionCount);
	}
	
	// Called by constructor to populate the HashMap
	public void HashMapPopulator()
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
	}
}
