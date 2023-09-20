import java.util.LinkedList;
import java.util.Optional;

public class Parser 
{
	private TokenHandler th;
	
	public Parser(LinkedList<Token> tokens)
	{
		th = new TokenHandler(tokens);
	}
	
	public ProgramNode Parse()
	{
		while (th.MoreTokens() == true)
		{
			
			if (ParseFunction() == true)
			{
				
			}
			
			else if (ParseAction() == true)
			{
				
			}
			
			else
				throw new UnsupportedOperationException("Unable to parse");
		}
	}
	
	private boolean ParseFunction(ProgramNode node)
	{
		
	}
	
	private boolean ParseAction(ProgramNode node)
	{
		
	}
	
	// Takes all separators in the list and moves past them
	private boolean AcceptSeparators()
	{
		// Saves the result from MatchAndRemove
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
		// Flags if a separator was found when MatchAndRemove was called.
		boolean hasSeparator = false;
		
		// If optionalToken has a separator token it sets flag as true.
		if (optionalToken.isPresent())
			hasSeparator = true;
		
		// Continues to remove separators until it returns an empty optional.
		while (optionalToken.isPresent())
		{
			optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
		}
		
		// Returns the flag which should return true when at least one separator is removed.
		return hasSeparator;
	}
	
	
}
