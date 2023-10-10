import java.util.LinkedList;
import java.util.Optional;

public class TokenHandler 
{
	private LinkedList<Token> tokens;
	
	public TokenHandler(LinkedList<Token> tokens)
	{
		this.tokens = tokens;
	}
	
	public Optional<Token> Peek(int j)
	{
		// Checks to make sure j is not out of bounds.
		if(j < 0 || j >= tokens.size())
			return Optional.empty();
		
		// returns Optional instance of the token
		return Optional.of(tokens.get(j));
	}
	
	// Tells if linked list of tokens is empty or not.
	public boolean MoreTokens()
	{
		if (tokens.isEmpty())
			return false; // If empty.
		else
			return true; // If not empty.
	}
	
	public Optional<Token> MatchAndRemove(Token.TokenType t)
	{
		if(MoreTokens())
		{
			if (tokens.getFirst().getType() == t)
			{
				Token tempToken = tokens.getFirst();
				tokens.removeFirst();
				return Optional.of(tempToken);
			}
		}
		
		return Optional.empty();
	}
}
