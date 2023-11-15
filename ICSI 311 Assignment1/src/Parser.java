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
		
		// Removes any possible separator at the beginning of the program.
		AcceptSeparators();
		
		// Loops as long as there are tokens to parse
		while (th.MoreTokens() == true)
		{
			// Continues while loop if ParseFunction was successful
			if (ParseFunction(node) == true)
				continue;
			// Continues while loop if ParseAction was successful
			else if (ParseAction(node) == true)
				continue;
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
		if (optionalToken.isPresent())
		{
			// Adds the first parameter to the list.
			ParameterNames.add(optionalToken.get().getValue());
			// Checks for parameter separator and if it exists will enter while loop until all parameter names are found.
			optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
			// Removes any possible separator after function comma.
			AcceptSeparators();
			
			// Loops as long as there are more parameters to add to the list.
			while (optionalToken.isPresent())
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
				// Removes any possible separator after parameter name.
				AcceptSeparators();
				
				// Makes sure that there is another parameter.
				if (optionalToken.isPresent())
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
		
		// Creates the BlockNode containing the statements (empty optional is put in place since conditional is only used in ParseAction).
		BlockNode block = ParseBlock(Optional.empty());
		LinkedList<StatementNode> statements = block.getStatements();
		
		// Initializes the FunctionDefinitionNode with all collected data.
		FDN = new FunctionDefinitionNode(statements, ParameterNames, FunctionName);
		
		// Adds the FunctionDefinitionNode to the linked list in ProgramNode.
		node.getFunctionDefinitionNode().add(FDN);
		
		// Removes any separators coming after a function so no issues arise when parsing the next chunk of code.
		AcceptSeparators();
		
		return true;
	}
	
	private boolean ParseAction(ProgramNode node)
	{
		// Initializes as empty in case action has no BEGIN or END.
		Optional<Token> optionalToken = Optional.empty();
		// Used to pass potential condition to ParseBlock.
		Optional<Node> optionalNode = Optional.empty();
		
		optionalToken = th.MatchAndRemove(Token.TokenType.BEGIN);
		if (optionalToken.isPresent() == true)
		{
			// Removes any possible separators after BEGIN keyword.
			AcceptSeparators();
			
			// Gives an empty optional as the condition.
			node.getBeginBlocks().add(ParseBlock(optionalNode));
			
			// Removes any possible separator after the block.
			AcceptSeparators();
			
			return true;
		}
	
		optionalToken = th.MatchAndRemove(Token.TokenType.END);
		if (optionalToken.isPresent() == true)
		{
			// Removes any possible separators after END keyword.
			AcceptSeparators();
			
			// Gives an empty optional as the condition.
			node.getEndBlocks().add(ParseBlock(optionalNode));
			
			// Removes any possible separator after the block.
			AcceptSeparators();
			
			return true;
		}
		
		// If action does not have a BEGIN or END.
		if (optionalToken.isPresent() == false)
		{
			// th.MatchAndRemove(Token.TokenType.WORD); // Strictly for JUnit test to get rid of "a" to prevent infinite loop in Parser 1.
			// Checks for a potential condition, if there isn't one it will remain empty and pass it to ParseBlock.
			optionalNode = ParseOperation();
			
			// Removes any possible separators.
			AcceptSeparators();
			
			// Handles edge case of an empty token list.
			if(th.MoreTokens() == true)
			{
				// Gives a potential condition, otherwise gives an empty.
				node.getBlocks().add(ParseBlock(optionalNode));
				
				// Removes any possible separator after the block.
				AcceptSeparators();
				
				return true;
			}
		}
		
		// Returns false if nothing is found.
		return false;
	}
	
	private BlockNode ParseBlock(Optional<Node> condition)
	{	
		Optional<Token> optionalToken = Optional.empty();
		
		Optional<Node> blockCondition = condition;
		BlockNode block = new BlockNode(blockCondition);
		
		// Checks if block has multiple lines.
		optionalToken = th.MatchAndRemove(Token.TokenType.OPENCURLBRACK);
		if(optionalToken.isPresent())
		{
			// Used to accept an empty block if the first result from ParseStatement is an empty optional.
			boolean EmptyBlockFlag = true;
			// Loops until all statements have been parsed.
			do
			{
				// Removes any possible separators.
				AcceptSeparators();
				
				Optional<StatementNode> optionalStatement = ParseStatement();
				// Stops the loop when the first thing is an empty statement and accepts it as an empty statement.
				if(optionalStatement.isEmpty() && EmptyBlockFlag == true)
				{
					block.getStatements().add(null);
					break;
				}
				// Stops the loop when an empty statement is returned.
				else if(optionalStatement.isEmpty() && EmptyBlockFlag == false)
				{
					break;
				}
				
				block.getStatements().add(optionalStatement.get());
				
				// Sets the flag to false after the first statement is successfully parse and present.
				if(EmptyBlockFlag == true)
					EmptyBlockFlag = false;
				
			}while(true);
			
			// Removes any possible separators.
			AcceptSeparators();
			
			// Makes sure block was closed.
			optionalToken = th.MatchAndRemove(Token.TokenType.CLOSECURLBRACK);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No closed curly brace found after block.");
		}
		// Processes single line blocks.
		else
		{
			// Removes any possible separators.
			AcceptSeparators();
			
			block.getStatements().add(ParseStatement().get());
		}
		
		return block;
	}
	
	private Optional<StatementNode> ParseStatement()
	{
		Optional<Token> optionalToken = Optional.empty();
		
		optionalToken = th.MatchAndRemove(Token.TokenType.CONTINUE);
		if(optionalToken.isPresent())
		{
			// Throws an exception when no separator follows the continue.
			if(AcceptSeparators() == false)
				throw new IllegalArgumentException("Must be a space following continue.");
			
			return Optional.of(new ContinueNode());
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.BREAK);
		if(optionalToken.isPresent())
		{
			// Throws an exception when no separator follows the break.
			if(AcceptSeparators() == false)
				throw new IllegalArgumentException("Must be a space following break.");
			
			return Optional.of(new BreakNode());
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.IF);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No open parenthesis found after if statement.");
			
			// Gets the condition of the if statement.
			Optional<Node> conditionNode = ParseOperation();
			if(conditionNode.isEmpty())
				throw new IllegalArgumentException("No condition given in if statement.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No closed parenthesis found after if statement.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			BlockNode statements = ParseBlock(Optional.empty());
			
			IfNode ifNode = new IfNode(conditionNode.get(), statements);
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Checks if there is an else statement following.
			optionalToken = th.MatchAndRemove(Token.TokenType.ELSE);
			if(optionalToken.isPresent())
			{
				// Removes any possible separator.
				AcceptSeparators();
				
				// Checks if it is an else-if statement.
				optionalToken = th.MatchAndRemove(Token.TokenType.IF);
				if(optionalToken.isPresent())
				{
					// Creates a temporary if node to hold current position of the chain (basically a linked list).
					IfNode current = ifNode;
					
					// Loops until all else-if/else statements are processed.
					while(optionalToken.isPresent())
					{
						// Removes any possible separator.
						AcceptSeparators();
						
						optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
						if(optionalToken.isEmpty())
							throw new IllegalArgumentException("No open parenthesis found after else if statement.");
						
						// Gets the condition of the if statement.
						conditionNode = ParseOperation();
						if(conditionNode.isEmpty())
							throw new IllegalArgumentException("No condition given in else if statement.");
						
						// Removes any possible separator.
						AcceptSeparators();
						
						optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
						if(optionalToken.isEmpty())
							throw new IllegalArgumentException("No closed parenthesis found after else if statement.");
						
						// Removes any possible separator.
						AcceptSeparators();
						
						statements = ParseBlock(Optional.empty());
						
						// Removes any possible separator.
						AcceptSeparators();
						
						// Adds the next else-if to the chain.
						current.addIfElse(new IfNode(conditionNode.get(), statements));
						// Sets current to the next IfNode in the chain.
						current = current.getNextIf();
						
						// Checks if there is another else statement.
						optionalToken = th.MatchAndRemove(Token.TokenType.ELSE);
						if(optionalToken.isPresent())
						{
							// Removes any possible separator.
							AcceptSeparators();
							
							// Checks for the if token. 
							optionalToken = th.MatchAndRemove(Token.TokenType.IF);
							// If it is empty, it will process as an else and end the loop, otherwise it will restart the loop.
							if(optionalToken.isEmpty())
							{	
								// Only gets the statements as an else statement has no condition.
								statements = ParseBlock(Optional.empty());
								// Adds the else node to the end of the chain.
								current.addIfElse(new IfNode(statements));
								
								// Removes any possible separator.
								AcceptSeparators();
								
								break; // exits the loop since there is no reason to continue.
							}
						}
						else
							break; // exits the loop since there is no reason to continue.
					}
				}
				
				// Processes the else statement if no if token was found.
				else
				{
					// Only gets the statements as an else statement has no condition.
					statements = ParseBlock(Optional.empty());
					
					// Adds the else node as the only next node in the IfNode (only an if else statement if it gets here).
					ifNode.addIfElse(new IfNode(statements));
				}
			}
			
			// Returns the head of the if statements.
			return Optional.of(ifNode);
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.FOR);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No open parenthesis found after for statement.");
			
			// Gets the condition of the for statement.
			Optional<Node> conditionNode = ParseOperation();
			if(conditionNode.isEmpty())
				throw new IllegalArgumentException("No condition given in for statement.");
			
			// Checks for a separator token to see the type of for loop.
			if(AcceptSeparators() == true)
			{
				// Gets the second condition of the for statement.
				Optional<Node> secondConditionNode = ParseOperation();
				if(secondConditionNode.isEmpty())
					throw new IllegalArgumentException("No second condition given in for statement.");
				
				// Makes sure there is a separator (;) between the second and third statement.
				if(AcceptSeparators() == true)
				{
					// Gets the third condition of the for statement.
					Optional<Node> thirdConditionNode = ParseOperation();
					if(thirdConditionNode.isEmpty())
						throw new IllegalArgumentException("No third condition given in for statement.");
					
					// Removes any possible separator.
					AcceptSeparators();
					
					// Checks for a closed parenthesis after the third statement.
					optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
					if(optionalToken.isEmpty())
						throw new IllegalArgumentException("No open parenthesis found after for statement.");
					
					// Removes any possible separator.
					AcceptSeparators();
					
					// Gets the statements of the for loop if any.
					BlockNode statements = ParseBlock(Optional.empty());
					
					// Returns the initialization, condition, increment, and statements in a ForNode.
					return Optional.of(new ForNode(conditionNode.get(), secondConditionNode.get(), thirdConditionNode.get(), statements));
				}
				// Throws an exception if no separator was found after getting the second condition.
				else
					throw new IllegalArgumentException("No sparator found after second condition of the for loop");
			}
			
			// For our version of AWK, for each strictly does not allow a separator after the first statement.
			// This would require taking in semi-colon as its own token which we did not do.
			else
			{
				// Removes any possible separator.
				AcceptSeparators();
				
				// Checks for a closed parenthesis after the first statement.
				optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
				if(optionalToken.isEmpty())
					throw new IllegalArgumentException("No closed parenthesis found after for each statement.");
				
				// Removes any possible separator.
				AcceptSeparators();
				
				// Gets the statements of the for loop if any.
				BlockNode statements = ParseBlock(Optional.empty());
				
				// Returns the only condition (key in array) and statements in a ForEachNode.
				return Optional.of(new ForEachNode(conditionNode.get(), statements));
			}
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.DELETE);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No array given after delete statement.");
			
			// Saves the array name.
			String arrayName = optionalToken.get().getValue();
			
			// Checks for an array reference list following an array name.
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENBRACK);
			if(optionalToken.isPresent())
			{
				// Removes any possible separator.
				AcceptSeparators();
				
				// Checks if an index exists.
				Optional<Node> indexExpression = ParseOperation();
				LinkedList<Node> indices = new LinkedList<Node>();
				
				// Adds the expression to the list if present.
				if(indexExpression.isPresent())
					indices.add(indexExpression.get());
				
				// Removes any possible separator.
				AcceptSeparators();
				
				// Checks if the list continues.
				optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
				if(optionalToken.isPresent())
				{
					// Takes care of an edge case such as delete array[,expression].
					if(indices.isEmpty())
						throw new IllegalArgumentException("Cannot enter a comma without an expression first when deleting an array.");
					
					do
					{
					// Removes any possible separator.
					AcceptSeparators();
					
					// Makes sure another expression exists after a comma.
					indexExpression = ParseOperation();
					if(indexExpression.isEmpty())
						throw new IllegalArgumentException("No expression found after comma when getting indices to delete.");
					
					// Adds the next expression to the list.
					indices.add(indexExpression.get());
					
					optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
					
					// Loops until no more commas are found.
					}while(optionalToken.isPresent());
				}
				// Removes any possible separator.
				AcceptSeparators();
				
				optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEBRACK);
				if(optionalToken.isEmpty())
					throw new IllegalArgumentException("No closing bracket found after getting indices to delete.");
				
				// Returns a DeleteNode that also contains indices to be deleted.
				return Optional.of(new DeleteNode(arrayName, Optional.of(indices)));
			}
			// Returns a DeleteNode if no open bracket is found (deletes whole array).
			else
				return Optional.of(new DeleteNode(arrayName));
			
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.WHILE);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			// Checks for an open parenthesis after the first statement.
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No open parenthesis found after while statement.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Gets the while loop condition.
			Optional<Node> condition = ParseOperation();
			if(condition.isEmpty())
				throw new IllegalArgumentException("No condition provided for while loop.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Checks for an open parenthesis after the first statement.
			optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No close parenthesis found after while statement.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			BlockNode statementsBlock = ParseBlock(Optional.empty());
			
			return Optional.of(new WhileNode(condition.get(), statementsBlock));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.DO);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			BlockNode statementsBlock = ParseBlock(Optional.empty());
			
			// Removes any possible separator.
			AcceptSeparators();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.WHILE);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No while statement found after do block");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Checks for an open parenthesis after the first statement.
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No open parenthesis found after do while statement.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Gets the while loop condition.
			Optional<Node> condition = ParseOperation();
			if(condition.isEmpty())
				throw new IllegalArgumentException("No condition provided for do while loop.");
			
			// Removes any possible separator.
			AcceptSeparators();
			
			// Checks for an open parenthesis after the first statement.
			optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
			if(optionalToken.isEmpty())
				throw new IllegalArgumentException("No close parenthesis found after do while statement.");
			
			return Optional.of(new DoWhileNode(condition.get(), statementsBlock));
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.RETURN);
		if(optionalToken.isPresent())
		{
			// Removes any possible separator.
			AcceptSeparators();
			
			// Gets the expression to be returned that follows the return statement.
			Optional<Node> returnExpression = ParseOperation();
			if(returnExpression.isEmpty())
				throw new IllegalArgumentException("Return must have an expression to return.");
			
			// Creates a new optional ReturnNode with the return expression (for example "return 1+2").
			if (returnExpression.isPresent())
				return Optional.of(new ReturnNode(returnExpression.get()));
			
			// If no expression was found, expression will be set to null, in the case of return without a parameter.
			else
				return Optional.of(new ReturnNode(null));
		}
		
		// Deals with operation statements (Assignment, increment and decrement, and function calls).
		else
		{
			Optional<Node> optionalNode = ParseOperation();
			if(optionalNode.isPresent())
			{
				// Checks if the result of ParseOperation is a function call.
				if(optionalNode.get() instanceof FunctionCallNode)
				{
					FunctionCallNode FuncCallNode = (FunctionCallNode) optionalNode.get();
					return Optional.of(FuncCallNode);
				}
				// Checks if the result of ParseOperation is an assignment.
				else if(optionalNode.get() instanceof AssignmentNode)
				{
					AssignmentNode asNode = (AssignmentNode) optionalNode.get();
					return Optional.of(asNode);
				}
				
				else
					throw new IllegalArgumentException("Invalide operation being used as statement.");
				
			}
		}
		
		// Returns empty when there is nothing in the block.
		return Optional.empty();
	}
	 
	private Optional<Node> ParseFunctionCall()
	{
		// Checks if there is a token to check.
		if(th.MoreTokens())
		// Checks for a name or system function then open parenthesis, then creates a FunctionCallNode to return.
			if(th.Peek(0).get().getType() == Token.TokenType.WORD 
			|| th.Peek(0).get().getType() == Token.TokenType.PRINT
			|| th.Peek(0).get().getType() == Token.TokenType.PRINTF
			|| th.Peek(0).get().getType() == Token.TokenType.EXIT
			|| th.Peek(0).get().getType() == Token.TokenType.GETLINE
			|| th.Peek(0).get().getType() == Token.TokenType.NEXTFILE
			|| th.Peek(0).get().getType() == Token.TokenType.NEXT)
			{ 
				// Checks for any extra separators and loops past them before checking for open Parenthesis.
				int i = 1;
				while(th.Peek(i).get().getType() == Token.TokenType.SEPARATOR)
				{
					i++;
					// Returns if it reached the end of the list
					if(th.Peek(i).isEmpty())
						return Optional.empty();
				}
				
				// Checks for a parenthesis for a potential parameter list.
				if(th.Peek(0).get().getType() == Token.TokenType.WORD)
				{
					if(th.Peek(i).get().getType() == Token.TokenType.OPENPAREN)
					{
						Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.WORD);
						// Saves the function name.
						String functionName = optionalToken.get().getValue();
						LinkedList<Node> parameters = new LinkedList<Node>();
						Optional<Node> parameterOperations;
						
						// Already checked for an open parenthesis so we can just remove it.
						th.MatchAndRemove(Token.TokenType.OPENPAREN);
						
						// Gets the first function parameter.
						parameterOperations = ParseOperation();
						
						if(parameterOperations.isPresent())
						{
							// Adds the first parameter into the linked list.
							parameters.add(parameterOperations.get());
							
							// Checks for more parameters.
							optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
							if(optionalToken.isPresent())
							{	
								// Loops until the full list is found.
								while(optionalToken.isPresent())
								{
									// Gets the next function parameter.
									parameterOperations = ParseOperation();
									
									// Adds the next parameter, throws exception if none was found.
									if(optionalToken.isPresent())
									{
										parameters.add(parameterOperations.get());
										
										// Checks for more parameters.
										optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
									}
									else
										throw new IllegalArgumentException("No more parameters found after comma in function call: " + functionName);
								}
							}
						}
						
						// Makes sure parameter list was closed.
						optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
						if(optionalToken.isEmpty())
							throw new IllegalArgumentException("No closed parenthesis was found in function call: " + functionName);
						
						// Returns the function call node after the name and parameter names have been collected.
						return Optional.of(new FunctionCallNode(functionName, parameters));
					}
				}
				
				else
				{
					Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.PRINT);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.PRINTF);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.EXIT);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.GETLINE);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.NEXTFILE);
					
					if(optionalToken.isEmpty())
						optionalToken = th.MatchAndRemove(Token.TokenType.NEXT);
					
					String functionName = "";
					
					if(optionalToken.get().getType() == Token.TokenType.PRINT)
						functionName = "print";
					
					if(optionalToken.get().getType() == Token.TokenType.PRINTF)
						functionName = "printf";
					
					if(optionalToken.get().getType() == Token.TokenType.EXIT)
						functionName = "exit";
					
					if(optionalToken.get().getType() == Token.TokenType.GETLINE)
						functionName = "getline";
					
					if(optionalToken.get().getType() == Token.TokenType.NEXTFILE)
						functionName = "nextfile";
					
					if(optionalToken.get().getType() == Token.TokenType.NEXT)
						functionName = "next";
						
					
					// Saves the function name.
					LinkedList<Node> parameters = new LinkedList<Node>();
					Optional<Node> parameterOperations;
					
					// Removes open parenthesis since in case it is present.
					optionalToken = th.MatchAndRemove(Token.TokenType.OPENPAREN);
					
					Boolean hasParen = false;
					// Sets flag to be used when checking for closed parenthesis.
					if(optionalToken.isPresent())
						hasParen = true;
					
					// Gets the first function parameter.
					parameterOperations = ParseOperation();
					
					if(parameterOperations.isPresent())
					{
						// Adds the first parameter into the linked list.
						parameters.add(parameterOperations.get());
						
						// Checks for more parameters.
						optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
						if(optionalToken.isPresent())
						{	
							// Loops until the full list is found.
							while(optionalToken.isPresent())
							{
								// Gets the next function parameter.
								parameterOperations = ParseOperation();
								
								// Adds the next parameter, throws exception if none was found.
								if(optionalToken.isPresent())
								{
									parameters.add(parameterOperations.get());
									
									// Checks for more parameters.
									optionalToken = th.MatchAndRemove(Token.TokenType.COMMA);
								}
								else
									throw new IllegalArgumentException("No more parameters found after comma in function call: " + functionName);
							}
						}
					}
					
					// Only checks for closed parenthesis if a open parenthesis was present.
					if(hasParen)
					{
						// Makes sure parameter list was closed.
						optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEPAREN);
						if(optionalToken.isEmpty())
							throw new IllegalArgumentException("No closed parenthesis was found in function call: " + functionName);
					}
					
					// Returns the function call node after the name and parameter names have been collected.
					return Optional.of(new FunctionCallNode(functionName, parameters));
				}
			}
		
		// Returns nothing if the function call conditions are not met
		return Optional.empty();
	}
	
	// Set public for testing purposes.
	private Optional<Node> ParseOperation()
	{
		return ParseAssignment();
	}
	
	private Optional<Node> ParseAssignment()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseTernary();

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
					
					if(rightNode.isPresent())
					{
						// Creates the AssignmentNode with correct operation (if any) depending on assignment type.
						if(optionalToken.get().getType() == Token.TokenType.EXPONENTEQUALS)
						{
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
							// Fetches the left value for OperationNode to prevent the value from being an AssignmentNode (in case of multiple assignments).
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
					}
					
					else
						throw new IllegalArgumentException("No expression found while assigning");
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
		
		do
		{
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.QUESTIONMARK); // Checks next token.
			
			if (optionalToken.isEmpty())
				return optionalNode;
			
			Optional<Node> trueCase = ParseTernary();
			
			if(trueCase.isPresent())
			{
				
				optionalToken = th.MatchAndRemove(Token.TokenType.COLON); // Checks next token.
				// Throws if there is no colon as it should not make it here if it is not a ternary operator.
				if (optionalToken.isEmpty())
					throw new IllegalArgumentException("No colon was found in ternary expression");
				
				Optional<Node> falseCase = ParseTernary();
				
				if(falseCase.isPresent())
					optionalNode = Optional.of(new TernaryNode(optionalNode.get(), trueCase.get(), falseCase.get()));
				else
					throw new IllegalArgumentException("No false case was found in ternary expression.");
			}
			else
				throw new IllegalArgumentException("No true case was found in ternary expression");
			
		}while(true);
	}
	
	private Optional<Node> ParseOr()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseAnd();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.OR);
		if (optionalToken.isPresent())
		{
			// Holds the second expression.
			Optional<Node> optNode = ParseOr();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.OR, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after || operator.");
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseAnd()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseArrayMembership();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.AND);
		if (optionalToken.isPresent())
		{
			// Holds the second expression.
			Optional<Node> optNode = ParseAnd();
			
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.AND, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after && operator.");
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseArrayMembership()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseMatch();
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.IN);
		if (optionalToken.isPresent())
		{
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
		
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.MATCH);
		if (optionalToken.isPresent())
		{
			// Holds the second expression.
			Optional<Node> optNode = ParseMatch();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.MATCH, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after ~ operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.NOTMATCH);
		if (optionalToken.isPresent())
		{
			// Holds the second expression.
			Optional<Node> optNode = ParseMatch();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.NOTMATCH, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after !~ operator.");
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseBooleanCompare()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseConcatenation();
		
		// Checks each possible Boolean compare and returns one if it is found.
		Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.GREATEROREQUAL);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.GE, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after >= operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.GREATERTHAN);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.GT, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after > operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.EQUALS);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.EQ, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after == operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.NOTEQUALS);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.NE, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after != operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.LESSOREQUAL);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.LE, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after <= operator.");
		}
		
		optionalToken = th.MatchAndRemove(Token.TokenType.LESSTHAN);
		if(optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseBooleanCompare();
			if(optNode.isPresent())
				return Optional.of(new OperationNode(OperationNode.operations.LT, optionalNode.get(), optNode));
			else
				throw new IllegalArgumentException("No second expression was found after < operator.");
		}
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
	}
	
	private Optional<Node> ParseConcatenation()
	{
		// Pushes it's way down the chain to the bottom and if any returns were found, stores it in optionalNode.
		Optional<Node> optionalNode = ParseExpression();
		
		// Checks if the right expression is an LValue to concatenate the string.
		Optional<Node> optNode = ParseExpression();
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
			
			return Optional.of(new OperationNode(OperationNode.operations.CONCATENATION, optionalNode.get(), optNode));
			
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
			
			// Gets right expression.
			Optional<Node> rightNode = ParseTerm();
			
			if(rightNode.isPresent())
			{
				// Returns an OperationNode depending on the token that was matched and removed.
				if(optionalToken.get().getType() == Token.TokenType.PLUS)
					optionalNode = Optional.of(new OperationNode(OperationNode.operations.ADD, optionalNode.get(), rightNode));
				else
					optionalNode = Optional.of(new OperationNode(OperationNode.operations.SUBTRACT, optionalNode.get(), rightNode));
			}
			// Throws an expression if no right expression was found including the operation.
			else
				throw new IllegalArgumentException("No right expression was found next to " + optionalToken.get().getType() + " operator.");
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

			// Gets right expression.
			Optional<Node> rightNode = ParseFactor();
			
			if(rightNode.isPresent())
			{
				// Returns an OperationNode depending on the token that was matched and removed.
				if(optionalToken.get().getType() == Token.TokenType.MULTIPLY)
					optionalNode = Optional.of(new OperationNode(OperationNode.operations.MULTIPLY, optionalNode.get(), rightNode));
				else if(optionalToken.get().getType() == Token.TokenType.DIVIDE)
					optionalNode = Optional.of(new OperationNode(OperationNode.operations.DIVIDE, optionalNode.get(), rightNode));
				else
					optionalNode = Optional.of(new OperationNode(OperationNode.operations.MODULO, optionalNode.get(), rightNode));
			}
			// Throws an expression if no right expression was found including the operation.
			else
				throw new IllegalArgumentException("No right expression was found next to " + optionalToken.get().getType() + " operator.");
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
		
		do
		{
			// Ensures there is an exponent operation.
			Optional<Token> optionalToken = th.MatchAndRemove(Token.TokenType.EXPONENT);
			
			if(optionalToken.isEmpty())
				return optionalNode;
			
			// Gets right expression.
			Optional<Node> rightNode = ParsePostIncrementAndDecrement();
			
			if(rightNode.isPresent())
				optionalNode = Optional.of(new OperationNode(OperationNode.operations.EXPONENT, optionalNode.get(), rightNode));
			// Throws an exception if there is no right expression.
			else
				throw new IllegalArgumentException("No second expression was found after != operator.");
			
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
			AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
			// Returns as an assignment node for ParseStatements.
			return Optional.of(asNode);
		}
		
		// Checks if a post decrement was found.
		optionalToken = th.MatchAndRemove(Token.TokenType.DECREMENT);
		if (optionalToken.isPresent())
		{
			OperationNode opNode = new OperationNode(OperationNode.operations.POSTDEC, optionalNode.get());
			AssignmentNode asNode = new AssignmentNode(optionalNode.get(), opNode);
			// Returns as an assignment node for ParseStatements.
			return Optional.of(asNode);
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
			// Holds name of variable.
			String name = optionalToken.get().getValue();
			
			optionalToken = th.MatchAndRemove(Token.TokenType.OPENBRACK);
			if (optionalToken.isPresent())
			{
				VariableReferenceNode vrNode = new VariableReferenceNode(name, ParseLValue());
				
				optionalToken = th.MatchAndRemove(Token.TokenType.CLOSEBRACK);
				
				// Makes sure there is a closing bracket when processing array;
				if(optionalToken.isEmpty())
					throw new IllegalArgumentException("No closing bracket was found in array.");
				
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
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("No operation found in parenthesis.");
			
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
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after increment.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.PREINC, optNode.get());
			AssignmentNode asNode = new AssignmentNode(optNode.get(), opNode);
			// Returns as an assignment node for ParseStatements.
			return Optional.of(asNode);
		}
		
		// Returns an Optional of OperationNode  of some operation and PREDEC.
		optionalToken = th.MatchAndRemove(Token.TokenType.DECREMENT);
		if (optionalToken.isPresent())
		{
			Optional<Node> optNode = ParseOperation();
			if (optNode.isEmpty())
				throw new IllegalArgumentException("Unable to find operation after decrement.");
			
			OperationNode opNode = new OperationNode(OperationNode.operations.PREDEC, optNode.get());
			AssignmentNode asNode = new AssignmentNode(optNode.get(), opNode);
			// Returns as an assignment node for ParseStatements.
			return Optional.of(asNode);
		}
		
		// Will check for a function call and return it if present, otherwise optionalNode is empty.
		optionalNode = ParseFunctionCall();
		
		// Returns an optionalNode containing whatever the previous method returned if no conditions are met.
		return optionalNode;
		
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
			
			// Continues to remove separators until the next token is no longer a separator.
			while (optionalToken.isPresent() == true)
			{
				optionalToken = th.MatchAndRemove(Token.TokenType.SEPARATOR);
			}
		}
		
		// Returns the flag which should return true when at least one separator is removed.
		return hasSeparator;
	}
}
