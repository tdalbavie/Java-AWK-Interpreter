import java.util.InputMismatchException;
import java.util.LinkedList;

public class Lexer 
{
	private StringHandler sh;
	
	public Lexer(String fileContents)
	{
		sh = new StringHandler(fileContents);
	}
	
	public LinkedList<Token> Lex()
	{
		// Holds length of string to make sure there is still data in StringHandler
		int stringLength = sh.Remainder().length() - 1;
		LinkedList<Token> tokens = new LinkedList<Token>();
		int lineNumber = 0;
		int charPosition = 0;
		
		// Makes sure that file is not already empty
		if (stringLength >= 0)
		{
			while (true)
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
					Token linefeed = new Token(sh.Peek(0), lineNumber, charPosition);
					tokens.add(linefeed);
					// Makes sure there is another character after linefeed otherwise exits loop
					if(sh.Remainder().length() - 1 > 0)
					{
						lineNumber++;
						charPosition = 0;
					}
					else
						break;
				}
				
				// Ignores carriage return like a whitespace.
				else if(sh.Peek(0) == '\r')
				{
					// Makes sure there is another character after carriage return otherwise exits loop
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
					Token word = ProcessWord(lineNumber, charPosition);
					tokens.add(word);
					// Makes sure there is another character after word otherwise exits loop
					if(sh.Remainder().length() - 1 > word.valueLength() - 1)
					{
						sh.Swallow(word.valueLength());
						charPosition += word.valueLength();
					}
					else
						break;
				}
				
				// Identifies the start of a number and loops until the full number is found.
				else if (Character.isDigit(sh.Peek(0)))
				{
					Token number = ProcessNumber(lineNumber, charPosition);
					tokens.add(number);
					// Makes sure there is another character after number otherwise exits loop
					if(sh.Remainder().length() - 1 > number.valueLength() - 1)
					{
						sh.Swallow(number.valueLength());
						charPosition += number.valueLength();
					}
					else
						break;
				}
				
				// Identifies the start of a decimal number until the full number is found.
				else if (sh.Peek(0) == '.')
				{
					Token decimal = ProcessNumber(lineNumber, charPosition);
					tokens.add(decimal);
					// Makes sure there is another character after decimal number otherwise exits loop
					if(sh.Remainder().length() - 1 > decimal.valueLength() - 1)
					{
						sh.Swallow(decimal.valueLength());
						charPosition += decimal.valueLength();
					}
					else
						break;
				}
				
				// Returns an error for unknown character and stops program.
				else
				{
					
				}
			}
		}
		
		return tokens;
	}
	
	// Peeks at following characters until full word is found.
	public Token ProcessWord(int lineNumber, int charPosition)
	{
		int i = 0;
		// Loops until it finds a character that does not match awk word syntax.
		while(Character.isAlphabetic(sh.Peek(i)) || Character.isDigit(sh.Peek(i)) || sh.Peek(i) == '_')
		{
			i++;
		}
		
		return new Token(sh.PeekString(i), lineNumber, charPosition);
	}
	
	// Peeks at following characters until full number is found.
	public Token ProcessNumber(int lineNumber, int charPosition)
	{
		int i = 0;
		// Loops until it finds a character that does not match awk number syntax.
		if (Character.isDigit(sh.Peek(i)))
		{
			while(Character.isDigit(sh.Peek(i)) || sh.Peek(i) == '.')
			{
				if(sh.Peek(i) == '.')
					break;
				else
					i++;
			}
		}
		
		// Continues loop if number has decimal or starts loop from decimal
		if (sh.Peek(i) == '.')
		{
			i++;
			while(Character.isDigit(sh.Peek(i)) || sh.Peek(i) == '.')
			{
				// Throws exception if it finds a number with more than one decimal.
				if(sh.Peek(i) == '.')
					 throw new InputMismatchException("Number has more than one decimal.");
				else
					i++;
			}
		}	
		
		return new Token(sh.PeekString(i), lineNumber, charPosition);
	}
}
