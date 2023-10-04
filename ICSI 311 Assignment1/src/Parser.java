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
				throw new UnsupportedOperationException("Unable to parse.");
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
		AcceptSeparators();
		
		// Holds the token to be checked before inserting into function definition node.
		Optional<Token> optionalToken;
		
		FunctionDefinitionNode FDN;
		String FunctionName;
		LinkedList<String> ParameterNames = new LinkedList<String>();
		
		// Takes in the function name.
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		// Removes any possible separator after function name.
		AcceptSeparators();
		
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
		AcceptSeparators();
		
		// Takes in the function parameter name (if there is one).
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		// Removes any possible separator after parameter name.
		AcceptSeparators();
		
		// Checks if there is a function parameter name and processes however many parameter names that were defined.
		if (optionalToken.isPresent() == true)
		{
			// Adds the first parameter to the list.
			ParameterNames.add(optionalToken.get().getValue());
			// Checks for parameter separator and if it exists will enter while loop until all parameter names are found.
			optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
			// Removes any possible separator after function comma.
			AcceptSeparators();
			
			// Loops as long as there are more parameters to add to the list.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
				// Removes any possible separator after parameter name.
				AcceptSeparators();
				
				// Makes sure that there is another parameter.
				if (optionalToken.isPresent() == true)
				{
					ParameterNames.add(optionalToken.get().getValue());
					// Checks if there is a comma then continue loop if present.
					optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
				}
				else
					throw new IllegalArgumentException("No parameter was found.");
					
				// Removes any possible separator after comma.
				AcceptSeparators();
			}
		}
		
		// Ensures function parameter list was closed.
		optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
		// Removes any possible separator after closed parenthesis in case statement block starts on a different line.
		AcceptSeparators();
		
		if (optionalToken.isPresent() == false)
			throw new IllegalArgumentException("Function call parameter was not closed.");
		
		// Creates the BlockNode containing the statements.
		BlockNode block = ParseBlock();
		LinkedList<StatementNode> statements = block.StatementsAccessor();
		
		// Initializes the FunctionDefinitionNode with all collected data.
		FDN = new FunctionDefinitionNode(statements, ParameterNames, FunctionName);
		
		// Adds the FunctionDefinitionNode to the linked list in ProgramNode.
		node.getFunctionDefinitionNode().add(FDN);
		
		return true;
	}
	
	private boolean ParseAction(ProgramNode node)
	{
		// Initializes as empty in case action has no BEGIN or END.
		Optional<Token> optionalToken = Optional.empty();
		// Ensures ParseOperation returns something.
		Optional<Node> optionalNode = Optional.empty();
		optionalToken = th.MatchAndRemove(Token.TokenType.BEGIN);
		boolean parsed = false;
		
		if (optionalToken.isPresent() == true)
		{
			node.getStartBlock().add(ParseBlock());
			parsed = true;
		}
	
		
		optionalToken = th.MatchAndRemove(Token.TokenType.END);
		
		if (optionalToken.isPresent() == true)
		{
			node.getEndBlock().add(ParseBlock());
			parsed = true;
		}
		
		// If action does not have a BEGIN or END.
		if (optionalToken.isPresent() == false)
		{
			//th.MatchAndRemove(Token.TokenType.WORD); // Strictly for JUnit test to get rid of "a" to prevent infinite loop.
			optionalNode = ParseOperation();
			node.getBlock().add(ParseBlock());
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
		return ParseAssignment();
	}
	
	private Optional<Node> ParseAssignment()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseTernary();
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.
		
		// Then checks if an assignment type is next.
		if (th.Peek(1).get().getType() == Token.TokenType.EQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.MINUSEQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.PLUSEQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.DIVIDEEQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.MULTIPLYEQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.MODEQUALS ||
				th.Peek(1).get().getType() == Token.TokenType.EXPONENTEQUALS)
		{
			
			// Checks for what type of assignment is being done.
			optionalToken = th.MatchAndRemove(Token.TokenType.EQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.EQ, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.MINUSEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.SUBTRACT, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.PLUSEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.ADD, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.DIVIDEEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.DIVIDE, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.MULTIPLYEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.MULTIPLY, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.MODEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.MODULO, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			optionalToken = th.MatchAndRemove(Token.TokenType.EXPONENTEQUALS);
			if (optionalToken.isPresent())
			{
				// Creates an operation node that contains the operation, lvalue, and following expression(s).
				OperationNode opNode = new OperationNode(OperationNode.operations.EXPONENT, optionalNode.get(), ParseOperation());
				AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
				return Optional.of(asNode);
			}
			
			// If it enters the two if statements and has no assignment operation for whatever reason it will throw an exception.
			else
			{
				throw new IllegalArgumentException("No assignment type was found.");
			}
		}
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met (if empty will cause an exception).
		return optionalNode;
	}
	
	private Optional<Node> ParseTernary()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseOr();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.QUESTIONMARK); // Checks next token.
		if (optionalToken.isPresent())
		{
			// Parses the true case expression in ternary operator.
			Optional<Node> trueCase = ParseOperation();
			// Ensures a colon is found before parsing next expression.
			if(th.MatchAndRemove(Token.TokenType.COLON).isEmpty())
				throw new IllegalArgumentException("No colon was found in ternary operator.");
			// Parses the false case expression in ternary operator.
			Optional<Node> falseCase = ParseOperation();
			// Returns an optional of a new TernaryNode.
			return Optional.of(new TernaryNode(optionalNode.get(), trueCase.get(), falseCase.get()));
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseOr()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseAnd();
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return Optional.empty();
	}
	
	private Optional<Node> ParseAnd()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseArrayMembership();
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseArrayMembership()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseMatch();
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseMatch()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseBooleanCompare();
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseBooleanCompare()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseConcatenation();
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseConcatenation()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseAnd();
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseExpression()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseExponents();
		Optional<Node> left = ParseTerm();
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.
		
		
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseTerm()
	{
		Optional<Node> left = ParseFactor();
		do
		{
			Optional<Token> op = th.MatchAndRemove(Token.TokenType.MULTIPLY); // Holds an operation.
			if (op.isEmpty())
				op = th.MatchAndRemove(Token.TokenType.DIVIDE);
			if (op.isEmpty())
				return left;
			Optional<Node> right = ParseFactor();
			if (op.get().getType() == Token.TokenType.MULTIPLY)
				left = Optional.of(new OperationNode(OperationNode.operations.MULTIPLY, left.get(), right));
			else
				left = Optional.of(new OperationNode(OperationNode.operations.DIVIDE, left.get(), right));
			
		}
		while(true);
	}
	
	private Optional<Node> ParseFactor()
	{
		Optional<Token> num = th.MatchAndRemove(Token.TokenType.NUMBER); // Holds a number if present.
		if (num.isPresent())
			return num;
		if (th.MatchAndRemove(Token.TokenType.OPENPAREN).isPresent())
		{
			Node exp = Expression();
			if (exp == null)
				throw new IllegalArgumentException("No expression was found while parsing a factor expression.");
			if (th.MatchAndRemove(Token.TokenType.CLOSEPAREN).isEmpty())
				throw new IllegalArgumentException("No closing parenthesis was found while parsing a factor expression.");
		}
	}
	
	private Optional<Node> ParseExponents()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParsePostIncrementAndDecrement();
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParsePostIncrementAndDecrement()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseBottomLevel();
		
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.

		
		// Checks if a post increment was found.
		optionalToken = th.MatchAndRemove(Token.TokenType.INCREMENT);
		if (optionalToken.isPresent())
		{
			OperationNode opNode = new OperationNode(OperationNode.operations.POSTINC, optionalNode.get());
			return Optional.of(opNode);
		}
		
		// Checks if a post decrement was found.
		optionalToken = th.MatchAndRemove(Token.TokenType.DECREMENT);
		if (optionalToken.isPresent())
		{
			OperationNode opNode = new OperationNode(OperationNode.operations.POSTDEC, optionalNode.get());
			return Optional.of(opNode);
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseBottomLevel()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseLValue();
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.
		
		// Returns an Optional of ConstantNode containing a string literal.
		optionalToken = th.MatchAndRemove(Token.TokenType.STRINGLITERAL);
		if (optionalToken.isPresent())
		{
			ConstantNode cn = new ConstantNode(optionalToken.get().getValue());
			return Optional.of(cn);
		}
		
		// Returns an Optional of ConstantNode containing a number.
		optionalToken = th.MatchAndRemove(Token.TokenType.NUMBER);
		if (optionalToken.isPresent())
		{
			ConstantNode cn = new ConstantNode(optionalToken.get().getValue());
			return Optional.of(cn);
		}
		
		// Returns an Optional of PatternNode.
		optionalToken = th.MatchAndRemove(Token.TokenType.PATTERN);
		if (optionalToken.isPresent())
		{
			PatternNode pn = new PatternNode(optionalToken.get().getValue());
			return Optional.of(pn);
		}
		
		// Returns an Optional of ParseOperation containing some operation between parenthesis.
		optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("No operation found in parenthesis.");
			
			// Removes any possible separator after operation is parsed.
			AcceptSeparators();
			// Makes sure there is a close parenthesis.
			optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
			if (optionalToken.isEmpty())
				throw new IllegalArgumentException("No closing parenthesis was found.");
			
			return optNode;
		}
		
		// Returns an Optional of OperationNode of some operation and NOT.
		optionalToken = th.MatchAndRemove(Token.TokenType.NOT);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after NOT.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after not.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.NOT, optNode.get());
			return Optional.of(opNode);
		}
		
		// Returns an Optional of OperationNode of some operation and UNARYNEG.
		optionalToken = th.MatchAndRemove(Token.TokenType.MINUS);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after MINUS.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after minus.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.UNARYNEG, optNode.get());
			return Optional.of(opNode);
		}
		
		// Returns an Optional of OperationNode of some operation and UNARYPOS.
		optionalToken = th.MatchAndRemove(Token.TokenType.PLUS);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after PLUS.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after plus.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.UNARYPOS, optNode.get());
			return Optional.of(opNode);
		}
		
		// Returns an Optional of OperationNode of some operation and PREINC.
		optionalToken = th.MatchAndRemove(Token.TokenType.INCREMENT);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after INCREMENT.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after increment.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.PREINC, optNode.get());
			return Optional.of(opNode);
		}
		
		// Returns an Optional of OperationNode  of some operation and PREDEC.
		optionalToken = th.MatchAndRemove(Token.TokenType.DECREMENT);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after DECREMENT.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after decrement.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.PREDEC, optNode.get());
			return Optional.of(opNode);
		}
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
		
	}
	
	
	private Optional<Node> ParseLValue()
	{
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.
		Optional<Node> optionalNode = Optional.empty(); // Ensures ParseOperation/ParseBottomLevel returns something.
		
		// Checks for field reference.
		optionalToken = th.MatchAndRemove(Token.TokenType.DOLLAR);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after dollar sign.
			AcceptSeparators();
			
			optionalNode = ParseBottomLevel();
			if (optionalNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after dollar.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.DOLLAR, optionalNode.get());
			return Optional.of(opNode);
		}
		
		// Checks for variable name.
		optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
		// Will either return VariableReferenceNode with variable name and expression of present.
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after variable name.
			AcceptSeparators();
			// Holds name of variable.
			String name = optionalToken.get().getValue();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENBRACK);
			if (optionalToken.isPresent())
			{
				// Removes any possible separator after open bracket.
				AcceptSeparators();
				
				VariableReferenceNode vrNode = new VariableReferenceNode(name, ParseBottomLevel());
				
				optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEBRACK);
				// Removes any possible separator after close bracket.
				AcceptSeparators();
				// Makes sure there is a closing bracket when processing array;
				if(optionalToken.isEmpty())
					throw new IllegalArgumentException("No closing bracket was found.");
				
				return Optional.of(vrNode);
			}
			// Runs if the next token is not an open bracket (array).
			else
			{
				VariableReferenceNode vrNode = new VariableReferenceNode(name);
				return Optional.of(vrNode);
			}
		}
		// Returns an empty optional if no conditions were met.
		return Optional.empty();
	}
	
	
	private BlockNode ParseBlock()
	{
		// Removes any possible separators after BEGIN or END keywords.
		AcceptSeparators();
		Optional<Node> Condition = Optional.empty();
		return new BlockNode(Condition);
	}
	
	// Takes all separators in the list and moves past them
	private boolean AcceptSeparators()
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
