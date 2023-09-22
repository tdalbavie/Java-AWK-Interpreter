import java.util.LinkedList;
import java.util.Optional;

public class Parser 
{
	private TokenHandler th;
	
	// Initializes the TokenHandler by passing the token list from Lexer.
	public Parser(LinkedList<Token> tokens)
	{
		th = new TokenHandler(tokens);
	}
	
	// S
	public ProgramNode Parse()
	{
		LinkedList<FunctionDefinitionNode> FDN = new LinkedList<FunctionDefinitionNode>();
		LinkedList<BlockNode> StartBlocks = new LinkedList<BlockNode>();
		LinkedList<BlockNode> EndBlocks = new LinkedList<BlockNode>();
		LinkedList<BlockNode> Blocks = new LinkedList<BlockNode>();
		// Contains the tree that will be returned 
		ProgramNode node = new ProgramNode(FDN, StartBlocks, EndBlocks, Blocks);
		
		while (th.MoreTokens() == true)
		{
			// Continues while loop if ParseFunction was successful
			if (ParseFunction(node) == true)
				continue;
			// Continues while loop if ParseAction was successful
			else if (ParseAction(node) == true)
				continue;
			// Throws an exception if code could not be parsed
			else
				throw new UnsupportedOperationException("Unable to parse");
		}
		
		return node;
	}
	
	private boolean ParseFunction(ProgramNode node)
	{
		// Checks to see if the next token is a function so it can get processed
		if (th.Peek(0).get().getType() != Token.TokenType.FUNCTION)
			return false;
		// Moves on to the name of the function
		else
			th.MatchAndRemove(Token.TokenType.FUNCTION);
		
		// Holds the token to be checked before inserting into function definition node.
		Optional<Token> optionalToken;
		
		FunctionDefinitionNode FDN;
		String FunctionName;
		LinkedList<String> ParameterNames = new LinkedList<String>();
		
		// Takes in the function name.
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		
		// Makes sure that MatchAndRemove returned a token otherwise throws an exception.
		if (optionalToken.isPresent() == true)
			FunctionName = optionalToken.get().getValue();
		else
			throw new IllegalArgumentException("No function name was found.");
		
		// Makes sure function parameter is correctly created.
		optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
		
		if (optionalToken.isPresent() == false)
			throw new IllegalArgumentException("No function call parameter was found.");
		
		// Is placed between every parameter name and comma to remove any possible new lines a user may have added.
		AcceptSeperators();
		
		// Takes in the function parameter name (if there is one).
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		
		AcceptSeperators();
		
		// Checks if there is a function parameter name and processes however many parameter names that were defined.
		if (optionalToken.isPresent() == true)
		{
			// Adds the first parameter to the list.
			ParameterNames.add(optionalToken.get().getValue());
			// Checks for parameter separator and if it exists will enter while loop until all parameter names are found.
			optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
			
			AcceptSeperators();
			
			// Loops as long as there are more parameters to add to the list.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
				
				AcceptSeperators();
				
				// Makes sure that there is another parameter.
				if (optionalToken.isPresent() == true)
				{
					ParameterNames.add(optionalToken.get().getValue());
					// Checks if there is a comma then continue loop if present.
					optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
				}
				else
					throw new IllegalArgumentException("No parameter was found");
					
				
				AcceptSeperators();
			}
			
			// Creates the BlockNode containing the statements.
			BlockNode block = ParseBlock();
			LinkedList<StatementNode> statements = block.StatementsAccessor();
			
			// Initializes the FunctionDefinitionNode with all collected data.
			FDN = new FunctionDefinitionNode(statements, ParameterNames, FunctionName);
			
			// Adds the FunctionDefinitionNode to the linked list in ProgramNode.
			node.FunctionDefinitionNodeAccessor().add(FDN);
		}
		
		// Ensures function parameter list was closed.
		optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
		
		if (optionalToken.isPresent() == false)
			throw new IllegalArgumentException("Function call parameter was not closed.");
		
		// Creates the new FunctionDefinitionNode after collecting all the funcion's information.
		FDN = new FunctionDefinitionNode(ParseBlock().StatementsAccessor(), ParameterNames, FunctionName);
		
		// Adds the FND to LinkedList of FND in the main ProgramNode.
		node.FunctionDefinitionNodeAccessor().add(FDN);
		
		return true;
	}
	
	private boolean ParseAction(ProgramNode node)
	{
		// initializes as empty in case action has no BEGIN or END.
		Optional<Token> optionalToken = Optional.empty();
		optionalToken = th.MatchAndRemove(Token.TokenType.BEGIN);
		boolean parsed = false;
		
		if (optionalToken.isPresent() == true)
		{
			node.StartBlockAccessor().add(ParseBlock());
			parsed = true;
		}
	
		
		optionalToken = th.MatchAndRemove(Token.TokenType.END);
		
		if (optionalToken.isPresent() == true)
		{
			node.EndBlockAccessor().add(ParseBlock());
			parsed = true;
		}
		
		// If action does not have a BEGIN or END.
		if (optionalToken.isPresent() == false)
		{
			ParseOperation();
			node.BlockAccessor().add(ParseBlock());
			parsed = true;
		}
		
		if (parsed == true)
			return true;
		else
			return false;
	}
	
	private Optional<Node> ParseOperation()
	{
		return Optional.empty();
	}
	
	private BlockNode ParseBlock()
	{
		LinkedList<StatementNode> Statements = new LinkedList<StatementNode>();
		Optional<Node> Condition = Optional.empty();
		return new BlockNode(Statements, Condition);
	}
	
	// Takes all separators in the list and moves past them
	private boolean AcceptSeperators()
	{
		// Flags if a separator was found when MatchAndRemove was called.
		boolean hasSeparator = false;
		
		// Only uses AcceptSeperators if there are more tokens
		if (th.MoreTokens() == true)
		{
			// Saves the result from MatchAndRemove
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
			
			// If optionalToken has a separator token it sets flag as true.
			if (optionalToken.isPresent() == true)
				hasSeparator = true;
			
			// Continues to remove separators until it returns an empty optional.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
			}
		}
		
		// Returns the flag which should return true when at least one separator is removed.
		return hasSeparator;
	}
	
	// Copy of AcceptSeperators strictly for use in JUnit tests, will be removed after.
	public boolean AcceptSeperators(TokenHandler th)
	{
		// Flags if a separator was found when MatchAndRemove was called.
		boolean hasSeparator = false;
		
		// Only uses AcceptSeperators if there are more tokens
		if (th.MoreTokens() == true)
		{
			// Saves the result from MatchAndRemove
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
			
			// If optionalToken has a separator token it sets flag as true.
			if (optionalToken.isPresent() == true)
				hasSeparator = true;
			
			// Continues to remove separators until it returns an empty optional.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
			}
		}
		
		// Returns the flag which should return true when at least one separator is removed.
		return hasSeparator;
	}
}
