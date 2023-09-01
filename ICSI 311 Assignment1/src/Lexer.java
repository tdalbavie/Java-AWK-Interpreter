import java.util.InputMismatchException;
import java.util.LinkedList;

public class Lexer 
{
	private StringHandler sh;
	private int lineNumber;
	private int charPosition;
	
	public Lexer(String fileContents)
	{
		sh = new StringHandler(fileContents);
		lineNumber = 0;
		charPosition = 0;
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
					Token linefeed = new Token(sh.Peek(0), lineNumber, charPosition);
					tokens.add(linefeed);
					sh.Swallow(1);
					lineNumber++;
					charPosition = 0;

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
		// Loops until it finds a character that does not match awk word syntax.
		while(Character.isAlphabetic(sh.Peek(0)) || Character.isDigit(sh.Peek(0)) || sh.Peek(0) == '_')
		{
			word += sh.GetChar();
			charPosition++;
		}
		
		return new Token(word, lineNumber, charPosition);
	}
	
	// Peeks at following characters until full number is found.
	public Token ProcessNumber()
	{
		// Holds the number to be added to the token
		String number = "";
		boolean foundPoint = false;
		
		// Loops until it finds a character that does not match awk number syntax.

		while(Character.isDigit(sh.Peek(0)) || sh.Peek(0) == '.')
		{
			if(foundPoint == true && sh.Peek(0) == '.')
			{
				throw new InputMismatchException("Number contains more than one decimal point");
			}
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
			
		return new Token(number, lineNumber, charPosition);
	}
}
