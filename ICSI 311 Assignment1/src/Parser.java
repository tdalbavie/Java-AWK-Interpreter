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
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();

		// A flag to check if what we are working with is a LValue.
		boolean flag = false;
		
		// Makes sure there is an expression to work with before checking instances.
		if (optionalNode.isPresent())
		{
			// First checks to see if the contents is an LValue.
			if (optionalNode.get() instanceof VariableReferenceNode)
			{
				flag = true;
			}
			// Only excepts an OperationNode if the operation is a field reference (from ParseLValue).
			else if(optionalNode.get() instanceof OperationNode)
			{
				OperationNode opNode = (OperationNode) optionalNode.get();
				if (opNode.getOperation() == OperationNode.operations.DOLLAR)
					flag = true;
			}
			
			if (flag == true)
			{			
				do
				{
					// Checks through a chain of if statements to see if the next token is an assignment operator, otherwise returns (in the case of stand alone LValues)
					Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.EXPONENTEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.MODEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.MULTIPLYEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.DIVIDEEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.PLUSEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.MINUSEQUALS);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.ASSIGN);
					
					if(optionalToken.isEmpty())
						return optionalNode;
					
					// Gets the right expression.
					Optional<Node> rightNode = ParseTernary();
					
					// Creates the AssignmentNode with correct operation (if any) depending on assignment type.
					if(optionalToken.get().getType() == Token.TokenType.EXPONENTEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.EXPONENT, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.EXPONENT, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.EXPONENT, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
						
					else if(optionalToken.get().getType() == Token.TokenType.MODEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.MODULO, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.MODULO, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.MODULO, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
					
					else if(optionalToken.get().getType() == Token.TokenType.MULTIPLYEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.MULTIPLY, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.MULTIPLY, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.MULTIPLY, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
					
					else if(optionalToken.get().getType() == Token.TokenType.DIVIDEEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.DIVIDE, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.DIVIDE, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.DIVIDE, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
					
					else if(optionalToken.get().getType() == Token.TokenType.PLUSEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.ADD, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.ADD, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.ADD, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
					
					else if(optionalToken.get().getType() == Token.TokenType.MINUSEQUALS)
					{
						// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode.
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								OperationNode opNode = new OperationNode(OperationNode.operations.SUBTRACT, tempOpNode.getRightNode().get(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								OperationNode opNode = new OperationNode(OperationNode.operations.SUBTRACT, asNode.getExpression(), rightNode);
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
							}
						}
						
						else
						{
							OperationNode opNode = new OperationNode(OperationNode.operations.SUBTRACT, optionalNode.get(), rightNode);
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), opNode));
						}
					}
					
					// Simply assigns with no operation (LValue = expression).
					else
					{
						if(optionalNode.get() instanceof AssignmentNode)
						{
							AssignmentNode asNode = (AssignmentNode) optionalNode.get();
							if(asNode.getExpression() instanceof OperationNode)
							{
								OperationNode tempOpNode = (OperationNode) asNode.getExpression();
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), tempOpNode.getRightNode().get()));
							}
							// In case the assignment did not have an operation (LValue = expression).
							else
							{
								optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), asNode.getExpression()));
							}
						}
						
						else
						{
							optionalNode = Optional.of(new AssignmentNode(optionalNode.get(), rightNode.get()));
						}
					}
				}while(true);
			}
		}
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met (if empty will cause an exception).
		return optionalNode;
	}
	
	private Optional<Node> ParseTernary()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseOr();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		do
		{
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.QUESTIONMARK); // Checks next token.
			
			if (optionalToken.isEmpty())
				return optionalNode;
			
			Optional<Node> trueCase = ParseTernary();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.COLON); // Checks next token.
			// Throws if there is no colon as it should not make it here if it is not a ternary operator.
			if (optionalToken.isEmpty())
				throw new IllegalArgumentException("No colon was found in conitional expression");
			
			Optional<Node> falseCase = ParseTernary();
			
			optionalNode = Optional.of(new TernaryNode(optionalNode.get(), trueCase.get(), falseCase.get()));
			
		}while(true);
	}
	
	private Optional<Node> ParseOr()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseAnd();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.OR);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			// Holds the second expression.
			Optional<Node> optNode = ParseOr();
			return Optional.of(new OperationNode(OperationNode.operations.OR, optionalNode.get(), optNode));
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseAnd()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseArrayMembership();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.AND);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			// Holds the second expression.
			Optional<Node> optNode = ParseAnd();
			return Optional.of(new OperationNode(OperationNode.operations.AND, optionalNode.get(), optNode));
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseArrayMembership()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseMatch();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.IN);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			// Calls ParseLValue to get a variable.
			Optional<Node> optNode = ParseLValue();
			if (optNode.get() instanceof VariableReferenceNode)
			{
				return Optional.of(new OperationNode(OperationNode.operations.IN, optionalNode.get(), optNode));

			}
			// Throws if return is not a VeriableReferenceNode.
			else
			{
				throw new IllegalArgumentException("No array found after in operation.");
			}
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseMatch()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseBooleanCompare();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.MATCH);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			// Holds the second expression.
			Optional<Node> optNode = ParseMatch();
			return Optional.of(new OperationNode(OperationNode.operations.MATCH, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.NOTMATCH);
		if (optionalToken.isPresent())
		{
			AcceptSeparators();
			// Holds the second expression.
			Optional<Node> optNode = ParseMatch();
			return Optional.of(new OperationNode(OperationNode.operations.NOTMATCH, optionalNode.get(), optNode));
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseBooleanCompare()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseConcatenation();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		// Checks each possible Boolean compare and returns one if it is found.
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.GREATEROREQUAL);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			return Optional.of(new OperationNode(OperationNode.operations.GE, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.GREATERTHAN);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			return Optional.of(new OperationNode(OperationNode.operations.GT, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.EQUALS);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			return Optional.of(new OperationNode(OperationNode.operations.EQ, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.NOTEQUALS);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			return Optional.of(new OperationNode(OperationNode.operations.NE, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.LESSOREQUAL);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			return Optional.of(new OperationNode(OperationNode.operations.LE, optionalNode.get(), optNode));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.LESSTHAN);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator after open parenthesis.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.LT, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No right expression found while parsing less than comparison.");
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseConcatenation()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseExpression();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		// Checks if the right expression is an LValue to concatenate the string.
		Optional<Node> optNode = ParseLValue();
		if (optNode.isPresent())
		{
			// Only excepts an OperationNode with field reference.
			if (optNode.get() instanceof OperationNode)
			{
				OperationNode opNode = (OperationNode) optNode.get();
				if (opNode.getOperation() == OperationNode.operations.DOLLAR);
				// Throws an exception in case any of the other Operations were attempted to be called in ParseBottomValue.
				else
					throw new IllegalArgumentException("Unable to concatinate a " + opNode.getOperation());
			}
			
			if (optNode.isPresent())
			{
				return Optional.of(new OperationNode(OperationNode.operations.CONCATENATION, optionalNode.get(), optNode));
			}
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseExpression()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseTerm();
		
		do
		{
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.PLUS);
			
			if(optionalToken.isEmpty())
				optionalToken = th.MatchAndRemove(Token.TokenType.MINUS);
			
			if(optionalToken.isEmpty())
				return optionalNode;
			
			Optional<Node> rightNode = ParseTerm();
			
			if(optionalToken.get().getType() == Token.TokenType.PLUS)
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.ADD, optionalNode.get(), rightNode));
			else
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.SUBTRACT, optionalNode.get(), rightNode));
			
		}while(true);
	}
	
	// Deals with multiplication, division, and modulo.
	private Optional<Node> ParseTerm()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseFactor();
		
		do
		{
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.MULTIPLY);
			
			if(optionalToken.isEmpty())
				optionalToken = th.MatchAndRemove(Token.TokenType.DIVIDE);
			
			if(optionalToken.isEmpty())
				optionalToken = th.MatchAndRemove(Token.TokenType.MODULO);
			
			if(optionalToken.isEmpty())
				return optionalNode;
			
			Optional<Node> rightNode = ParseFactor();
			
			if(optionalToken.get().getType() == Token.TokenType.MULTIPLY)
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.MULTIPLY, optionalNode.get(), rightNode));
			else if(optionalToken.get().getType() == Token.TokenType.DIVIDE)
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.DIVIDE, optionalNode.get(), rightNode));
			else
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.MODULO, optionalNode.get(), rightNode));
			
		}while(true);
	}
	
	// Doesn't do much since all the factoring is being done under this method, only implemented for rubric sake.
	private Optional<Node> ParseFactor()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseExponents();
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseExponents()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParsePostIncrementAndDecrement();
		
		// Removes any possible separator after open parenthesis.
		AcceptSeparators();
		
		do
		{
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.EXPONENT);
			
			if(optionalToken.isEmpty())
				return optionalNode;
			
			Optional<Node> rightNode = ParsePostIncrementAndDecrement();
			
			optionalNode = Optional.of(new OperationNode(OperationNode.operations.EXPONENT, optionalNode.get(), rightNode));
			
		}while(true);
	}
	
	private Optional<Node> ParsePostIncrementAndDecrement()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseLValue();
		
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
	
	private Optional<Node> ParseLValue()
	{
		Optional<Node> optionalNode = ParseBottomLevel(); // Ensures ParseOperation/ParseBottomLevel returns something.
		Optional<Token> optionalToken = Optional.empty(); // Checks next token.
		
		// Returns optionalNode if it a Constant for concatenation reasons.
		if (optionalNode.isPresent())
			if (optionalNode.get() instanceof ConstantNode)
				return optionalNode;
		
		// Checks for field reference.
		optionalToken = th.MatchAndRemove(Token.TokenType.DOLLAR);
		if (optionalToken.isPresent())
		{
			// Removes any possible separator after dollar sign.
			AcceptSeparators();
			
			Optional<Node> optNode = ParseLValue();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after dollar.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.DOLLAR, optNode.get());
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
		return optionalNode;
	}
	
	private Optional<Node> ParseBottomLevel()
	{
		// Returns empty if nothing is processed in the bottom level.
		Optional<Node> optionalNode = Optional.empty();
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
