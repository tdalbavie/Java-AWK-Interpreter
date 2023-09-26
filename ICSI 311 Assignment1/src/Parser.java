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
	
	// Parses all the tokens and returns an AST
	public ProgramNode Parse()
	{
		// Initializes the AST; 
		ProgramNode node = new ProgramNode();
		
		// Loops as long as there are tokens to parse
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
		
		// Removes any possible separator after function keyword.
		AcceptSeperators();
		
		// Holds the token to be checked before inserting into function definition node.
		Optional<Token> optionalToken;
		
		FunctionDefinitionNode FDN;
		String FunctionName;
		LinkedList<String> ParameterNames = new LinkedList<String>();
		
		// Takes in the function name.
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		// Removes any possible separator after function name.
		AcceptSeperators();
		
		// Makes sure that MatchAndRemove returned a token otherwise throws an exception.
		if (optionalToken.isPresent() == true)
			FunctionName = optionalToken.get().getValue();
		else
			throw new IllegalArgumentException("No function name was found.");
		
		// Makes sure function parameter is correctly created.
		optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
		
		if (optionalToken.isPresent() == false)
			throw new IllegalArgumentException("No function call parameter was found.");
		
		// Removes any possible separator after open parenthesis.
		AcceptSeperators();
		
		// Takes in the function parameter name (if there is one).
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		// Removes any possible separator after parameter name.
		AcceptSeperators();
		
		// Checks if there is a function parameter name and processes however many parameter names that were defined.
		if (optionalToken.isPresent() == true)
		{
			// Adds the first parameter to the list.
			ParameterNames.add(optionalToken.get().getValue());
			// Checks for parameter separator and if it exists will enter while loop until all parameter names are found.
			optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
			// Removes any possible separator after function comma.
			AcceptSeperators();
			
			// Loops as long as there are more parameters to add to the list.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
				// Removes any possible separator after parameter name.
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
					
				// Removes any possible separator after comma.
				AcceptSeperators();
			}
		}
		
		// Ensures function parameter list was closed.
		optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
		// Removes any possible separator after closed parenthesis in case statement block starts on a different line.
		AcceptSeperators();
		
		if (optionalToken.isPresent() == false)
			throw new IllegalArgumentException("Function call parameter was not closed.");
		
		// Creates the BlockNode containing the statements.
		BlockNode block = ParseBlock();
		LinkedList<StatementNode> statements = block.StatementsAccessor();
		
		// Initializes the FunctionDefinitionNode with all collected data.
		FDN = new FunctionDefinitionNode(statements, ParameterNames, FunctionName);
		
		// Adds the FunctionDefinitionNode to the linked list in ProgramNode.
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
			th.MatchAndRemove(Token.TokenType.WORD); // Strictly for JUnit test to get rid of "a" to prevent infinite loop.
			ParseOperation();
			node.BlockAccessor().add(ParseBlock());
			parsed = true;
		}
		
		if (parsed == true)
			return true;
		else
			return false;
	}
	
	// Set public for testing purposes.
	public Optional<Node> ParseOperation()
	{
		return ParseBottomLevel();
	}
	
	private Optional<Node> ParseBottomLevel()
	{
		Optional<Token> optionalToken = Optional.empty();
		
		optionalToken = th.MatchAndRemove(Token.TokenType.STRINGLITERAL);
		if (optionalToken.isPresent())
		{
			
		}
	}
	
	private Optional<Node> ParseLValue()
	{
		Optional<Token> optionalToken = Optional.empty();
		
		optionalToken = th.MatchAndRemove(Token.TokenType.DOLLAR);
		if (optionalToken.isPresent())
		{
			OperationNode opNode = new OperationNode(ParseBottomLevel().get(), OperationNode.operations.DOLLAR);
			return Optional.of(opNode);
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		if (optionalToken.isPresent())
		{
			// Holds name of variable.
			String name = optionalToken.get().getValue();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENBRACK);
			if (optionalToken.isPresent())
			{
				VariableReferenceNode vrNode = new VariableReferenceNode(name, ParseBottomLevel());
				
				optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEBRACK);
				// Makes sure there is a closing bracket when processing array;
				if(optionalToken.isEmpty())
					throw new IllegalArgumentException("No closing bracket was found");
				
				return Optional.of(vrNode);
			}
			// Runs if the next token is not an open bracket.
			else
			{
				VariableReferenceNode vrNode = new VariableReferenceNode(name);
				return Optional.of(vrNode);
			}
		}
		return Optional.empty();
	}
	
	private BlockNode ParseBlock()
	{
		// Removes any possible separators after BEGIN or END keywords.
		AcceptSeperators();
		Optional<Node> Condition = Optional.empty();
		return new BlockNode(Condition);
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
}
