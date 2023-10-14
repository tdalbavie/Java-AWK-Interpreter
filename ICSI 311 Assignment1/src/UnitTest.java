import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{
	@Test
	public void BreakTest()
	{
		Lexer lexer = new Lexer("break\r\n");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    // BreakNode is an empty class so it only needs to be present in the list.
	    Assert.assertTrue(block.getStatements().get(0) instanceof BreakNode);
	}
	
	@Test
	public void ContinueTest()
	{
		Lexer lexer = new Lexer("{continue\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    // ContinueNode is an empty class so it only needs to be present in the list.
	    Assert.assertTrue(block.getStatements().get(0) instanceof ContinueNode);
	}
	
	@Test
	public void ifStatementTest()
	{
		// Used function named printer since print function and other system functions are created in Interpreter 1.
		Lexer lexer = new Lexer("{\r\n"
				+ "    if ($1 == \"A\") {\r\n"
				+ "        printer(\"The input is A.\")\r\n"
				+ "    } else if ($1 == \"B\") {\r\n"
				+ "        printer(\"The input is B.\")\r\n"
				+ "    } else if ($1 == \"C\") {\r\n"
				+ "        printer(\"The input is C.\")\r\n"
				+ "    } else {\r\n"
				+ "        printer(\"The input is not A, B, or C.\")\r\n"
				+ "    }\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    
	    // Checks the first if statement
	    IfNode ifNode = (IfNode) block.getStatements().get(0);
	    // Assume operation node contains ($1 == A)
	    Assert.assertTrue(ifNode.getCondition().isPresent());
	    Assert.assertTrue(ifNode.getCondition().get() instanceof OperationNode);
	    // I will only check statements once for brevity, assume the rest are true.
	    BlockNode ifBlock = (BlockNode) ifNode.getStatements();
	    FunctionCallNode ifFCN = (FunctionCallNode) ifBlock.getStatements().get(0);
	    Assert.assertEquals(ifFCN.getName(), "printer");
	    ConstantNode ifConstNode = (ConstantNode) ifFCN.getParameterNames().get(0);
	    Assert.assertEquals(ifConstNode.getConstantValue(), "The input is A.");
	    
	    // Checks second if statement.
	    IfNode ifElse1Node = ifNode.getNextIf();
	    Assert.assertTrue(ifElse1Node.getCondition().isPresent());
	    // Assume this shows that the linked list in the block node contains a proper FunctionCallNode.
	    Assert.assertTrue(ifElse1Node.getStatements().getStatements().isEmpty() == false);
	    
	    // Checks third if statement.
	    IfNode ifElse2Node = ifElse1Node.getNextIf();
	    Assert.assertTrue(ifElse2Node.getCondition().isPresent());
	    // Assume this shows that the linked list in the block node contains a proper FunctionCallNode.
	    Assert.assertTrue(ifElse2Node.getStatements().getStatements().isEmpty() == false);
	    
	    IfNode elseNode = ifElse2Node.getNextIf();
	    // Should have no condition as it is an else statement.
	    Assert.assertTrue(elseNode.getCondition().isEmpty());
	    // Assume this shows that the linked list in the block node contains a proper FunctionCallNode.
	    Assert.assertTrue(elseNode.getStatements().getStatements().isEmpty() == false);
	}
	

	
	@Test
	public void ForTest()
	{
		Lexer lexer = new Lexer("{\r\n"
				+ "    for (i = 1; i <= 5; i++) {\r\n"
				+ "        printer (\"Iteration\", i)\r\n"
				+ "        i++\r\n"
				+ "    }\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    ForNode forNode = (ForNode) block.getStatements().get(0);
	    // Assume AssignmentNode contains the first condition (i = 1).
	    Assert.assertTrue(forNode.getInitialization() instanceof AssignmentNode);
	    // Assume OperationNode contains the second condition (i <= 5).
	    Assert.assertTrue(forNode.getCondition() instanceof OperationNode);
	    // Assume AssignmentNode contains the third condition (i++).
	    Assert.assertTrue(forNode.getIncrement() instanceof AssignmentNode);
	    
	    BlockNode forBlock = forNode.getStatements();
	    FunctionCallNode fcNode = (FunctionCallNode) forBlock.getStatements().get(0);
	    Assert.assertEquals(fcNode.getName(), "printer");
	    // Assume ConstantNode contains the first parameter of function printer ("Iteration")
	    Assert.assertTrue(fcNode.getParameterNames().get(0) instanceof ConstantNode);
	    // Assume VariableReferenceNode contains the second parameter of function printer (i)
	    Assert.assertTrue(fcNode.getParameterNames().get(1) instanceof VariableReferenceNode);
	    // Assume AssignmentNode contains the second statement in the if block (i++)
	    Assert.assertTrue(forBlock.getStatements().get(1) instanceof AssignmentNode);
	    		
	}
	
	@Test
	public void ForEachTest()
	{
		Lexer lexer = new Lexer("{\r\n"
				+ "    for (key in myArray) {\r\n"
				+ "        printer (\"Key:\", key, \"Value:\", myArray[key])\r\n"
				+ "    }}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    ForEachNode feNode = (ForEachNode) block.getStatements().get(0);
	    
	    // Assume OperationNode contains (key in myArray).
	    Assert.assertTrue(feNode.getArrayMembershipCondition() instanceof OperationNode);
	    BlockNode feBlock = feNode.getStatements();
	    FunctionCallNode fcNode = (FunctionCallNode) feBlock.getStatements().get(0);
	    Assert.assertEquals(fcNode.getName(), "printer");
	    // Assume this contains the first parameter ("Key: ").
	    Assert.assertTrue(fcNode.getParameterNames().get(0) instanceof ConstantNode);
	    // Assume this contains the second parameter (key).
	    Assert.assertTrue(fcNode.getParameterNames().get(1) instanceof VariableReferenceNode);
	    // Assume this contains the third parameter ("Value: ).
	    Assert.assertTrue(fcNode.getParameterNames().get(2) instanceof ConstantNode);
	    // Assume this contains the fourth parameter (myArray[key]).
	    Assert.assertTrue(fcNode.getParameterNames().get(3) instanceof VariableReferenceNode);
	}
	
	@Test
	public void DeleteTest()
	{
		Lexer lexer = new Lexer("{delete myArray[a, b]}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlock().get(0);
	    DeleteNode delNode = (DeleteNode) block.getStatements().get(0);
	    
	    Assert.assertEquals(delNode.getArrayName(), "myArray");
	    VariableReferenceNode vrNode1 = (VariableReferenceNode) delNode.getIndex().get(0);
	    VariableReferenceNode vrNode2 = (VariableReferenceNode) delNode.getIndex().get(1);
	    
	    Assert.assertEquals(vrNode1.getName(), "a");
	    Assert.assertEquals(vrNode2.getName(), "b");
	}
	
	@Test
	public void WhileTest()
	{
		Lexer lexer = new Lexer("BEGIN {\r\n"
				+ "    i = 1\r\n"
				+ "    while (i <= 5) {\r\n"
				+ "        i++\r\n"
				+ "    }\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode startBlock = node.getStartBlock().get(0);
	    // Assume this AssignmentNode contains (i = 1).
	    Assert.assertTrue(startBlock.getStatements().get(0) instanceof AssignmentNode);
	    
	    WhileNode whileNode = (WhileNode) startBlock.getStatements().get(1);
	    // Assume OperationNode contains the operation of while (i <= 5).
	    Assert.assertTrue(whileNode.getCondition() instanceof OperationNode);
	    BlockNode whileBlock = whileNode.getStatements();
	    // Assume this AssignmentNode contains (i++).
	    Assert.assertTrue(whileBlock.getStatements().get(0) instanceof AssignmentNode);
	}
	
	@Test
	public void DoWhileTest()
	{
		Lexer lexer = new Lexer("BEGIN {\r\n"
				+ "    i = 1\r\n"
				+ "    do {\r\n"
				+ "        i++\r\n"
				+ "    } while (i <= 5)\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode startBlock = node.getStartBlock().get(0);
	    // Assume this AssignmentNode contains (i = 1).
	    Assert.assertTrue(startBlock.getStatements().get(0) instanceof AssignmentNode);
	    
	    DoWhileNode whileNode = (DoWhileNode) startBlock.getStatements().get(1);
	    // Assume OperationNode contains the operation of while (i <= 5).
	    Assert.assertTrue(whileNode.getCondition() instanceof OperationNode);
	    BlockNode whileBlock = whileNode.getStatements();
	    // Assume this AssignmentNode contains (i++).
	    Assert.assertTrue(whileBlock.getStatements().get(0) instanceof AssignmentNode);
	}
	
	@Test
	public void ReturnTest()
	{
		Lexer lexer = new Lexer("function square(num1, num2) {\r\n"
				+ "    return num1 * num2\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    FunctionDefinitionNode funcDefNode = node.getFunctionDefinitionNode().getFirst();
	    Assert.assertEquals(funcDefNode.getFunctionName(), "square");
	    Assert.assertEquals(funcDefNode.getParameterNames().get(0), "num1");
	    Assert.assertEquals(funcDefNode.getParameterNames().get(1), "num2");
	    
	    // We will assume the operation node is correct which I have verified in the debugger to contain (num1 * num2).
	    ReturnNode returnNode = (ReturnNode) funcDefNode.getStatements().get(0);
	    Assert.assertTrue(returnNode.getReturnExpression() instanceof OperationNode);
	}
	
	// All tests past this point are for parser 3
	@Test
	public void AssignmentTest()
	{
		Lexer lexer = new Lexer("a += b -= 5");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks down Assignment Node
	    AssignmentNode asNode = (AssignmentNode) parser.ParseOperation().get();
	    
	    // Checks the first OperationNode (b - 5)
	    OperationNode subtractionNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(subtractionNode.getOperation(), OperationNode.operations.SUBTRACT);
	    ConstantNode rightConstantNode = (ConstantNode) subtractionNode.getRightNode().get();
	    Assert.assertEquals(rightConstantNode.getConstantValue(), "5");
	    VariableReferenceNode leftSubtractionNode = (VariableReferenceNode) subtractionNode.getLeftNode();
	    Assert.assertEquals(leftSubtractionNode.getName(), "b");
	    
	    AssignmentNode nestedAsNode = (AssignmentNode) asNode.getTarget();
	    
	    // Checks the second OperationNode (a + b)
	    OperationNode additionNode = (OperationNode) nestedAsNode.getExpression();
	    Assert.assertEquals(additionNode.getOperation(), OperationNode.operations.ADD);
	    VariableReferenceNode rightAdditionNode = (VariableReferenceNode) additionNode.getRightNode().get();
	    Assert.assertEquals(rightAdditionNode.getName(), "b");
	    VariableReferenceNode leftAdditionNode = (VariableReferenceNode) additionNode.getLeftNode();
	    Assert.assertEquals(leftAdditionNode.getName(), "a");
	    
	    // Gets the anticipated target from the nested AssignmentNode.
	    VariableReferenceNode targetVrNode = (VariableReferenceNode) nestedAsNode.getTarget();
	    Assert.assertEquals(targetVrNode.getName(), "a");
	}
	
	@Test
	public void TernaryTest()
	{
		Lexer lexer = new Lexer("a > b ? a > c ? a : c : b");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    TernaryNode terNode = (TernaryNode) parser.ParseOperation().get();
	    
	    // Checks first condition OperationNode (a > b).
	    OperationNode terCondition = (OperationNode) terNode.getExpression();
	    Assert.assertEquals(terCondition.getOperation(), OperationNode.operations.GT);
	    VariableReferenceNode leftTerCondition = (VariableReferenceNode) terCondition.getLeftNode();
	    VariableReferenceNode rightTerCondition = (VariableReferenceNode) terCondition.getRightNode().get();
	    Assert.assertEquals(leftTerCondition.getName(), "a");
	    Assert.assertEquals(rightTerCondition.getName(), "b");
	    
	    // Checks false case (b at the end of string).
	    VariableReferenceNode falseTerNode = (VariableReferenceNode) terNode.getFalseCase();
	    Assert.assertEquals(falseTerNode.getName(), "b");
	    
	    // Checks true case (a > b ? a : c).
	    TernaryNode nestedTerNode = (TernaryNode) terNode.getTrueCase();
	    
	    // Checks inner ternary node condition (a > c)
	    OperationNode nestedTerCondition = (OperationNode) nestedTerNode.getExpression();
	    Assert.assertEquals(nestedTerCondition.getOperation(), OperationNode.operations.GT);
	    VariableReferenceNode nestedLeftTerCondition = (VariableReferenceNode) nestedTerCondition.getLeftNode();
	    VariableReferenceNode nestedRightTerCondition = (VariableReferenceNode) nestedTerCondition.getRightNode().get();
	    Assert.assertEquals(nestedLeftTerCondition.getName(), "a");
	    Assert.assertEquals(nestedRightTerCondition.getName(), "c");
	    
	    // Checks inner false case (c)
	    VariableReferenceNode nestedFalseTerNode = (VariableReferenceNode) nestedTerNode.getFalseCase();
	    Assert.assertEquals(nestedFalseTerNode.getName(), "c");
	    
	    // Checks inner true case (a)
	    VariableReferenceNode nestedTrueTerNode = (VariableReferenceNode) nestedTerNode.getTrueCase();
	    Assert.assertEquals(nestedTrueTerNode.getName(), "a");
	    
	}
	
	@Test
	public void OrBooleanTest()
	{
		Lexer lexer = new Lexer("(a < 10) || (a > 20)");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.OR);
	    
	    OperationNode leftOpNode = (OperationNode) opNode.getLeftNode();
	    OperationNode rightOpNode = (OperationNode) opNode.getRightNode().get();
	    
	    Assert.assertEquals(leftOpNode.getOperation(), OperationNode.operations.LT);
	    Assert.assertEquals(rightOpNode.getOperation(), OperationNode.operations.GT);
	    
	    VariableReferenceNode leftVrNode = (VariableReferenceNode) leftOpNode.getLeftNode();
	    ConstantNode leftConstNode = (ConstantNode) leftOpNode.getRightNode().get();
	    
	    Assert.assertEquals(leftVrNode.getName(), "a");
	    Assert.assertEquals(leftConstNode.getConstantValue(), "10");
	    
	    VariableReferenceNode rightVrNode = (VariableReferenceNode) rightOpNode.getLeftNode();
	    ConstantNode rightConstNode = (ConstantNode) rightOpNode.getRightNode().get();
	    
	    Assert.assertEquals(rightVrNode.getName(), "a");
	    Assert.assertEquals(rightConstNode.getConstantValue(), "20");
	}
	
	@Test
	public void AndBooleanTest()
	{
		Lexer lexer = new Lexer("(a >= 10) && (a <= 20)");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.AND);
	    
	    OperationNode leftOpNode = (OperationNode) opNode.getLeftNode();
	    OperationNode rightOpNode = (OperationNode) opNode.getRightNode().get();
	    
	    Assert.assertEquals(leftOpNode.getOperation(), OperationNode.operations.GE);
	    Assert.assertEquals(rightOpNode.getOperation(), OperationNode.operations.LE);
	    
	    VariableReferenceNode leftVrNode = (VariableReferenceNode) leftOpNode.getLeftNode();
	    ConstantNode leftConstNode = (ConstantNode) leftOpNode.getRightNode().get();
	    
	    Assert.assertEquals(leftVrNode.getName(), "a");
	    Assert.assertEquals(leftConstNode.getConstantValue(), "10");
	    
	    VariableReferenceNode rightVrNode = (VariableReferenceNode) rightOpNode.getLeftNode();
	    ConstantNode rightConstNode = (ConstantNode) rightOpNode.getRightNode().get();
	    
	    Assert.assertEquals(rightVrNode.getName(), "a");
	    Assert.assertEquals(rightConstNode.getConstantValue(), "20");
	}
	
	@Test
	public void ArrayMembershipTest()
	{
		Lexer lexer = new Lexer("a in b");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.IN);
	    
	    VariableReferenceNode leftVrNode = (VariableReferenceNode) opNode.getLeftNode();
	    VariableReferenceNode rightVrNode = (VariableReferenceNode) opNode.getRightNode().get();
	    
	    Assert.assertEquals(leftVrNode.getName(), "a");
	    Assert.assertEquals(rightVrNode.getName(), "b");
	}
	
	@Test
	public void MatchTest()
	{
		Lexer lexer = new Lexer("($1 ~ `hello`) && ($2 !~ `world`)");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.AND);
	    
	    // Breaks down left side of the comparison.
	    OperationNode leftMatch = (OperationNode) opNode.getLeftNode();
	    Assert.assertEquals(leftMatch.getOperation(), OperationNode.operations.MATCH);
	    OperationNode leftFieldRef = (OperationNode) leftMatch.getLeftNode();
	    Assert.assertEquals(leftFieldRef.getOperation(), OperationNode.operations.DOLLAR);
	    ConstantNode leftConst = (ConstantNode) leftFieldRef.getLeftNode();
	    Assert.assertEquals(leftConst.getConstantValue(), "1");
	    PatternNode leftPattern = (PatternNode) leftMatch.getRightNode().get();
	    Assert.assertEquals(leftPattern.getPattern(), "hello");
	    
	    // Breaks down right side of the comparison.
	    OperationNode rightMatch = (OperationNode) opNode.getRightNode().get();
	    Assert.assertEquals(rightMatch.getOperation(), OperationNode.operations.NOTMATCH);
	    OperationNode rightFieldRef = (OperationNode) rightMatch.getLeftNode();
	    Assert.assertEquals(rightFieldRef.getOperation(), OperationNode.operations.DOLLAR);
	    ConstantNode rightConst = (ConstantNode) rightFieldRef.getLeftNode();
	    Assert.assertEquals(rightConst.getConstantValue(), "2");
	    PatternNode rightPattern = (PatternNode) rightMatch.getRightNode().get();
	    Assert.assertEquals(rightPattern.getPattern(), "world");
	}
	
	@Test
	public void ConcatenationTest()
	{
		Lexer lexer = new Lexer("\"The value of A is: \" A");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.CONCATENATION);
	    
	    ConstantNode constNode = (ConstantNode) opNode.getLeftNode();
	    VariableReferenceNode vrNode = (VariableReferenceNode) opNode.getRightNode().get();
	    
	    Assert.assertEquals(constNode.getConstantValue(), "The value of A is: ");
	    Assert.assertEquals(vrNode.getName(), "A");
	}
	
	@Test
	public void ExpressionTermFactorTest()
	{
		Lexer lexer = new Lexer("6 / 2 * 1 + 2"); // Parenthesis needs to be changed.
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks down the division.
	    OperationNode additionNode = (OperationNode) parser.ParseOperation().get();
	    Assert.assertEquals(additionNode.getOperation(), OperationNode.operations.ADD);
	    ConstantNode rightAdditionConst = (ConstantNode) additionNode.getRightNode().get();
	    Assert.assertEquals(rightAdditionConst.getConstantValue(), "2");
	    
	    // Breaks down the multiplication.
	    OperationNode multiplyNode = (OperationNode) additionNode.getLeftNode();
	    Assert.assertEquals(multiplyNode.getOperation(), OperationNode.operations.MULTIPLY);
	    ConstantNode rightMultiplyConst = (ConstantNode) multiplyNode.getRightNode().get();
	    Assert.assertEquals(rightMultiplyConst.getConstantValue(), "1");
	    
	    // Breaks down the addition.
	    OperationNode divideNode = (OperationNode) multiplyNode.getLeftNode();
	    Assert.assertEquals(divideNode.getOperation(), OperationNode.operations.DIVIDE);
	    ConstantNode rightDivideConst = (ConstantNode) divideNode.getRightNode().get();
	    ConstantNode leftDivideConst = (ConstantNode) divideNode.getLeftNode();
	    Assert.assertEquals(rightDivideConst.getConstantValue(), "2");
	    Assert.assertEquals(leftDivideConst.getConstantValue(), "6");
	}
	
	@Test
	public void ExponentsTest()
	{
		Lexer lexer = new Lexer("a^2^3");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.EXPONENT);
	    
	    ConstantNode rightConstNode = (ConstantNode) opNode.getRightNode().get();
	    
	    Assert.assertEquals(rightConstNode.getConstantValue(), "3");
	    
	    OperationNode leftOpNode = (OperationNode) opNode.getLeftNode();
	    Assert.assertEquals(leftOpNode.getOperation(), OperationNode.operations.EXPONENT);
	    
	    ConstantNode nestedRightConstNode = (ConstantNode) leftOpNode.getRightNode().get();
	    VariableReferenceNode vrNode = (VariableReferenceNode) leftOpNode.getLeftNode();
	    
	    Assert.assertEquals(nestedRightConstNode.getConstantValue(), "2");
	    Assert.assertEquals(vrNode.getName(), "a");
	    
	}
	
	@Test
	public void PostIncrementAndDecrementTest()
	{
		Lexer lexer = new Lexer("(a--)++");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    AssignmentNode asNode = (AssignmentNode) parser.ParseOperation().get();
	    
	    OperationNode incrementOpNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(incrementOpNode.getOperation(), OperationNode.operations.POSTINC);
	    // Only checks if there is an assignment node as it is a copy of what is in target of asNode.
	    Assert.assertTrue(incrementOpNode.getLeftNode() instanceof AssignmentNode);
	    
	    AssignmentNode nestedAsNode = (AssignmentNode) asNode.getTarget();
	    OperationNode decrementOpNode = (OperationNode) nestedAsNode.getExpression();
	    Assert.assertEquals(decrementOpNode.getOperation(), OperationNode.operations.POSTDEC);
	    VariableReferenceNode decrementVrNode = (VariableReferenceNode) decrementOpNode.getLeftNode();
	    Assert.assertEquals(decrementVrNode.getName(), "a");
	    
	    VariableReferenceNode nestedVrNode = (VariableReferenceNode) nestedAsNode.getTarget();
	    Assert.assertEquals(nestedVrNode.getName(), "a");
	}
	
	// All tests past this point are for parser 2
	@Test
	public void OperationsTest1()
	{
		Lexer lexer = new Lexer("++a");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    AssignmentNode asNode = (AssignmentNode) parser.ParseOperation().get();
	    
	    VariableReferenceNode assignmentVrNode = (VariableReferenceNode) asNode.getTarget();
	    Assert.assertEquals(assignmentVrNode.getName(), "a");
	    
	    OperationNode opNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.PREINC);
	    VariableReferenceNode vrNode = (VariableReferenceNode) opNode.getLeftNode();
	    Assert.assertEquals(vrNode.getName(), "a");
	    
	    
	}
	@Test
	public void OperationsTest2()
	{
		Lexer lexer = new Lexer("++$b");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    AssignmentNode asNode = (AssignmentNode) parser.ParseOperation().get();
	    
	    OperationNode assignmentOpNode = (OperationNode) asNode.getTarget();
	    Assert.assertEquals(assignmentOpNode.getOperation(), OperationNode.operations.DOLLAR);
	    VariableReferenceNode assignmentVrNode = (VariableReferenceNode) assignmentOpNode.getLeftNode();
	    Assert.assertEquals(assignmentVrNode.getName(), "b");
	    
	    OperationNode opNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.PREINC);
	    OperationNode nestedOpNode = (OperationNode) opNode.getLeftNode();
	    Assert.assertEquals(nestedOpNode.getOperation(), OperationNode.operations.DOLLAR);
	    VariableReferenceNode vrNode = (VariableReferenceNode) nestedOpNode.getLeftNode();
	    Assert.assertEquals(vrNode.getName(), "b");
	}
	
	@Test
	public void OperationsTest3()
	{
		Lexer lexer = new Lexer("(++d)");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    AssignmentNode asNode = (AssignmentNode) parser.ParseOperation().get();
	    
	    VariableReferenceNode assignmentVrNode = (VariableReferenceNode) asNode.getTarget();
	    Assert.assertEquals(assignmentVrNode.getName(), "d");
	    
	    OperationNode opNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.PREINC);
	    VariableReferenceNode vrNode = (VariableReferenceNode) opNode.getLeftNode();
	    Assert.assertEquals(vrNode.getName(), "d");
	}
	
	@Test
	public void OperationsTest4()
	{
		Lexer lexer = new Lexer("-5");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.UNARYNEG);
	    
	    ConstantNode constNode = (ConstantNode) opNode.getLeftNode();
	    Assert.assertEquals(constNode.getConstantValue(), "5");
	}

	@Test
	public void OperationsTest5()
	{
		Lexer lexer = new Lexer("`[abc]`");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    PatternNode patNode = (PatternNode) parser.ParseOperation().get();
	    Assert.assertEquals(patNode.getPattern(), "[abc]");
	}

	@Test
	public void OperationsTest6()
	{
		Lexer lexer = new Lexer("e[++b]");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    VariableReferenceNode vrNode = (VariableReferenceNode) parser.ParseOperation().get();
	    Assert.assertEquals(vrNode.getName(), "e");
	    
	    AssignmentNode asNode = (AssignmentNode) vrNode.getIndex().get();
	    VariableReferenceNode assignmentVrNode = (VariableReferenceNode) asNode.getTarget();
	    Assert.assertEquals(assignmentVrNode.getName(), "b");
	    
	    OperationNode opNode = (OperationNode) asNode.getExpression();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.PREINC);
	    VariableReferenceNode assignementOperationVrNode = (VariableReferenceNode) opNode.getLeftNode();
	    Assert.assertEquals(assignementOperationVrNode.getName(), "b");
	}

	@Test
	public void OperationsTest7()
	{
		Lexer lexer = new Lexer("$7");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    
	    // Breaks apart the tree to test the contents in each node.
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	    Assert.assertEquals(opNode.getOperation(), OperationNode.operations.DOLLAR);
	    
	    ConstantNode constNode = (ConstantNode) opNode.getLeftNode();
	    Assert.assertEquals(constNode.getConstantValue(), "7");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest1()
	{
		// Throws when no closed parenthesis is found
		Lexer lexer = new Lexer("(++a");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest2()
	{
		// Throws when nothing is found after increment.
		Lexer lexer = new Lexer("++");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest3()
	{
		// Throws when nothing is found after decrement.
		Lexer lexer = new Lexer("--");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest4()
	{
		// Throws when nothing is found after positive.
		Lexer lexer = new Lexer("+");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest5()
	{
		// Throws when nothing is found after negative.
		Lexer lexer = new Lexer("-");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest6()
	{
		// Throws when nothing is found after not.
		Lexer lexer = new Lexer("!");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest7()
	{
		// Throws when nothing is found after dollar.
		Lexer lexer = new Lexer("$");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void FailedOperationsTest8()
	{
		// Throws when nothing is found in parenthesis.
		Lexer lexer = new Lexer("()");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    OperationNode opNode = (OperationNode) parser.ParseOperation().get();
	}
	
	/*
	// All tests past this point are for Parser 1.
	@Test
	public void TokenHandlerPeekTest()
	{
		Lexer lex = new Lexer("$0 = tolower($0)");
        TokenHandler th = new TokenHandler(lex.Lex());
        
        Assert.assertEquals(Token.TokenType.DOLLAR, th.Peek(0).get().getType());
        Assert.assertEquals(Token.TokenType.NUMBER, th.Peek(1).get().getType());
        Assert.assertEquals(Token.TokenType.ASSIGN, th.Peek(2).get().getType());
        Assert.assertEquals(Token.TokenType.WORD, th.Peek(3).get().getType());
        Assert.assertEquals(Token.TokenType.OPENPAREN, th.Peek(4).get().getType());
        Assert.assertEquals(Token.TokenType.DOLLAR, th.Peek(5).get().getType());
        Assert.assertEquals(Token.TokenType.NUMBER, th.Peek(6).get().getType());
        Assert.assertEquals(Token.TokenType.CLOSEPAREN, th.Peek(7).get().getType());
        Assert.assertEquals(Token.TokenType.SEPARATOR, th.Peek(8).get().getType());
	}
	
	@Test
	public void TokenHandlerMatchAndRemoveAndMoreTokensTest()
	{
		Lexer lex = new Lexer("$0 = tolower($0)");
        TokenHandler th = new TokenHandler(lex.Lex());
        
        Assert.assertEquals(true, th.MoreTokens());
        
        Assert.assertEquals(Token.TokenType.DOLLAR, th.MatchAndRemove(Token.TokenType.DOLLAR).get().getType());
        Assert.assertEquals(Token.TokenType.NUMBER, th.MatchAndRemove(Token.TokenType.NUMBER).get().getType());
        Assert.assertEquals(Token.TokenType.ASSIGN, th.MatchAndRemove(Token.TokenType.ASSIGN).get().getType());
        Assert.assertEquals(Token.TokenType.WORD, th.MatchAndRemove(Token.TokenType.WORD).get().getType());
        Assert.assertEquals(Token.TokenType.OPENPAREN, th.MatchAndRemove(Token.TokenType.OPENPAREN).get().getType());
        Assert.assertEquals(Token.TokenType.DOLLAR, th.MatchAndRemove(Token.TokenType.DOLLAR).get().getType());
        Assert.assertEquals(Token.TokenType.NUMBER, th.MatchAndRemove(Token.TokenType.NUMBER).get().getType());
        Assert.assertEquals(Token.TokenType.CLOSEPAREN, th.MatchAndRemove(Token.TokenType.CLOSEPAREN).get().getType());
        Assert.assertEquals(Token.TokenType.SEPARATOR, th.MatchAndRemove(Token.TokenType.SEPARATOR).get().getType());
        
        Assert.assertEquals(false, th.MoreTokens());
	}
	
	@Test
	public void parseFunctionTest()
	{
		Lexer lex = new Lexer("function myFunction(a, b)");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.getFunctionDefinitionNode().getFirst().FunctionNameAccessor());
	    Assert.assertEquals("a", node.getFunctionDefinitionNode().getFirst().ParameterNamesAccessor().get(0));
	    Assert.assertEquals("b", node.getFunctionDefinitionNode().getFirst().ParameterNamesAccessor().get(1));
	}
	
	@Test
	public void parseMultipleFunctionTest()
	{
		Lexer lex = new Lexer("function myFunction(a, b)\nfunction myOtherFunction(c, d)");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.getFunctionDefinitionNode().get(0).FunctionNameAccessor());
	    Assert.assertEquals("a", node.getFunctionDefinitionNode().get(0).ParameterNamesAccessor().get(0));
	    Assert.assertEquals("b", node.getFunctionDefinitionNode().get(0).ParameterNamesAccessor().get(1));
	    
	    Assert.assertEquals("myOtherFunction", node.getFunctionDefinitionNode().get(1).FunctionNameAccessor());
	    Assert.assertEquals("c", node.getFunctionDefinitionNode().get(1).ParameterNamesAccessor().get(0));
	    Assert.assertEquals("d", node.getFunctionDefinitionNode().get(1).ParameterNamesAccessor().get(1));
	}
	
	@Test
	public void parseFunctionNoParametersTest()
	{
		Lexer lex = new Lexer("function myFunction()");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.getFunctionDefinitionNode().getFirst().FunctionNameAccessor());
	    Assert.assertTrue(node.getFunctionDefinitionNode().getFirst().ParameterNamesAccessor().isEmpty());
	}
	
	// Same test as before just with lots of separators.
	@Test
	public void parseFunctionWithSeperatorsTest()
	{
		Lexer lex = new Lexer("function\n\n\n myFunction\n\n\n(\n\n\na\n\n,\n\n b\n\n\n)\n\n");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.getFunctionDefinitionNode().getFirst().FunctionNameAccessor());
	    Assert.assertEquals("a", node.getFunctionDefinitionNode().getFirst().ParameterNamesAccessor().get(0));
	    Assert.assertEquals("b", node.getFunctionDefinitionNode().getFirst().ParameterNamesAccessor().get(1));
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionBEGINTest()
	{
		Lexer lex = new Lexer("BEGIN");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals(Optional.empty(), node.getStartBlock().getFirst().ConditionAccessor());
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionENDTest()
	{
		Lexer lex = new Lexer("END");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    
	    Assert.assertEquals(Optional.empty(), node.getEndBlock().getFirst().ConditionAccessor());
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionOtherTest()
	{
		Lexer lex = new Lexer("a");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    
	    Assert.assertEquals(Optional.empty(), node.getBlock().getFirst().ConditionAccessor());
	}
	

	// All tests past this point are for Lexer.
	@Test
	public void singleLineTokenTest()
    {
        Lexer lex = new Lexer("$0 = tolower($0)");
        LinkedList<Token> tokens = lex.Lex();
        
        Assert.assertEquals(9, tokens.size());
        Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(0).getType());
        Assert.assertEquals(Token.TokenType.NUMBER, tokens.get(1).getType());
        Assert.assertEquals(Token.TokenType.ASSIGN, tokens.get(2).getType());
        Assert.assertEquals(Token.TokenType.WORD, tokens.get(3).getType());
        Assert.assertEquals("tolower", tokens.get(3).getValue());
        Assert.assertEquals(Token.TokenType.OPENPAREN, tokens.get(4).getType());
        Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(5).getType());
        Assert.assertEquals(Token.TokenType.NUMBER, tokens.get(6).getType());
        Assert.assertEquals(Token.TokenType.CLOSEPAREN, tokens.get(7).getType());
        Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(8).getType());
    }
	
	@Test
	public void singleLinePositionTest()
    {
        Lexer lex = new Lexer("$0 = tolower($0)");
        LinkedList<Token> tokens = lex.Lex();
        
        Assert.assertEquals(1, tokens.get(0).getCharPosition());
        Assert.assertEquals(2, tokens.get(1).getCharPosition());
        Assert.assertEquals(4, tokens.get(2).getCharPosition());
        Assert.assertEquals(6, tokens.get(3).getCharPosition());
        Assert.assertEquals(13, tokens.get(4).getCharPosition());
        Assert.assertEquals(14, tokens.get(5).getCharPosition());
        Assert.assertEquals(15, tokens.get(6).getCharPosition());
        Assert.assertEquals(16, tokens.get(7).getCharPosition());
        Assert.assertEquals(17, tokens.get(8).getCharPosition());
    }
	
	@Test 
	public void patternsTokenTest()
	{
		Lexer lex = new Lexer("`\"start\"`, `\"end\"` { print }");
		LinkedList<Token> tokens = lex.Lex();
		
		Assert.assertEquals(7, tokens.size());
		Assert.assertEquals(Token.TokenType.PATTERN, tokens.get(0).getType());
		Assert.assertEquals("\"start\"", tokens.get(0).getValue());
		Assert.assertEquals(Token.TokenType.COMMA, tokens.get(1).getType());
		Assert.assertEquals(Token.TokenType.PATTERN, tokens.get(2).getType());
		Assert.assertEquals("\"end\"", tokens.get(2).getValue());
		Assert.assertEquals(Token.TokenType.OPENCURLBRACK, tokens.get(3).getType());
		Assert.assertEquals(Token.TokenType.PRINT, tokens.get(4).getType());
		Assert.assertEquals(Token.TokenType.CLOSECURLBRACK, tokens.get(5).getType());
		Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(6).getType());
	}
	
	@Test 
	public void patternsPositionTest()
	{
		Lexer lex = new Lexer("`\"start\"`, `\"end\"` { print }");
		LinkedList<Token> tokens = lex.Lex();
		
		Assert.assertEquals(1, tokens.get(0).getCharPosition());
		Assert.assertEquals(10, tokens.get(1).getCharPosition());
		Assert.assertEquals(12, tokens.get(2).getCharPosition());
		Assert.assertEquals(20, tokens.get(3).getCharPosition());
		Assert.assertEquals(22, tokens.get(4).getCharPosition());
		Assert.assertEquals(28, tokens.get(5).getCharPosition());
		Assert.assertEquals(29, tokens.get(6).getCharPosition());
	}
	
	@Test
	public void literalStringTokenTest()
	{
		Lexer lex = new Lexer("if ($0 ~ \"This is a \\\"quoted\\\" string.\") print message2");
		LinkedList<Token> tokens = lex.Lex();

		Assert.assertEquals(10, tokens.size());
		Assert.assertEquals(Token.TokenType.IF, tokens.get(0).getType());
		Assert.assertEquals(Token.TokenType.OPENPAREN, tokens.get(1).getType());
		Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(2).getType());
		Assert.assertEquals(Token.TokenType.NUMBER, tokens.get(3).getType());
		Assert.assertEquals("0", tokens.get(3).getValue());
		Assert.assertEquals(Token.TokenType.MATCH, tokens.get(4).getType());
		Assert.assertEquals(Token.TokenType.STRINGLITERAL, tokens.get(5).getType());
		Assert.assertEquals("This is a \"quoted\" string.", tokens.get(5).getValue());
		Assert.assertEquals(Token.TokenType.CLOSEPAREN, tokens.get(6).getType());
		Assert.assertEquals(Token.TokenType.PRINT, tokens.get(7).getType());
		Assert.assertEquals(Token.TokenType.WORD, tokens.get(8).getType());
		Assert.assertEquals("message2", tokens.get(8).getValue());
		Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(9).getType());
	}
	
	@Test
	public void literalStringPositionTest()
	{
		Lexer lex = new Lexer("if ($0 ~ \"This is a \\\"quoted\\\" string.\") print message2");
		LinkedList<Token> tokens = lex.Lex();

		Assert.assertEquals(1, tokens.get(0).getCharPosition());
		Assert.assertEquals(4, tokens.get(1).getCharPosition());
		Assert.assertEquals(5, tokens.get(2).getCharPosition());
		Assert.assertEquals(6, tokens.get(3).getCharPosition());
		Assert.assertEquals(8, tokens.get(4).getCharPosition());
		Assert.assertEquals(10, tokens.get(5).getCharPosition());
		Assert.assertEquals(38, tokens.get(6).getCharPosition());
		Assert.assertEquals(40, tokens.get(7).getCharPosition());
		Assert.assertEquals(46, tokens.get(8).getCharPosition());
		Assert.assertEquals(54, tokens.get(9).getCharPosition());
	}
	
	@Test
	public void tokenSymbolTest()
	{
		Lexer lex = new Lexer(">= ++ -- <= == != ^= %= *= /= += -= !~ && >> || "
				+ "{} [] () $ ~ = < > ! + ^ - ? : * / % ; \n | ,");
		LinkedList<Token> tokens = lex.Lex();
		
		Assert.assertEquals(Token.TokenType.GREATEROREQUAL, tokens.get(0).getType());
		Assert.assertEquals(Token.TokenType.INCREMENT, tokens.get(1).getType());
		Assert.assertEquals(Token.TokenType.DECREMENT, tokens.get(2).getType());
		Assert.assertEquals(Token.TokenType.LESSOREQUAL, tokens.get(3).getType());
		Assert.assertEquals(Token.TokenType.EQUALS, tokens.get(4).getType());
		Assert.assertEquals(Token.TokenType.NOTEQUALS, tokens.get(5).getType());
		Assert.assertEquals(Token.TokenType.EXPONENTEQUALS, tokens.get(6).getType());
		Assert.assertEquals(Token.TokenType.MODEQUALS, tokens.get(7).getType());
		Assert.assertEquals(Token.TokenType.MULTIPLYEQUALS, tokens.get(8).getType());
		Assert.assertEquals(Token.TokenType.DIVIDEEQUALS, tokens.get(9).getType());
		Assert.assertEquals(Token.TokenType.PLUSEQUALS, tokens.get(10).getType());
		Assert.assertEquals(Token.TokenType.MINUSEQUALS, tokens.get(11).getType());
		Assert.assertEquals(Token.TokenType.NOTMATCH, tokens.get(12).getType());
		Assert.assertEquals(Token.TokenType.AND, tokens.get(13).getType());
		Assert.assertEquals(Token.TokenType.APPEND, tokens.get(14).getType());
		Assert.assertEquals(Token.TokenType.OR, tokens.get(15).getType());
		Assert.assertEquals(Token.TokenType.OPENCURLBRACK, tokens.get(16).getType());
		Assert.assertEquals(Token.TokenType.CLOSECURLBRACK, tokens.get(17).getType());
		Assert.assertEquals(Token.TokenType.OPENBRACK, tokens.get(18).getType());
		Assert.assertEquals(Token.TokenType.CLOSEBRACK, tokens.get(19).getType());
		Assert.assertEquals(Token.TokenType.OPENPAREN, tokens.get(20).getType());
		Assert.assertEquals(Token.TokenType.CLOSEPAREN, tokens.get(21).getType());
		Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(22).getType());
		Assert.assertEquals(Token.TokenType.MATCH, tokens.get(23).getType());
		Assert.assertEquals(Token.TokenType.ASSIGN, tokens.get(24).getType());
		Assert.assertEquals(Token.TokenType.LESSTHAN, tokens.get(25).getType());
		Assert.assertEquals(Token.TokenType.GREATERTHAN, tokens.get(26).getType());
		Assert.assertEquals(Token.TokenType.NOT, tokens.get(27).getType());
		Assert.assertEquals(Token.TokenType.PLUS, tokens.get(28).getType());
		Assert.assertEquals(Token.TokenType.EXPONENT, tokens.get(29).getType());
		Assert.assertEquals(Token.TokenType.MINUS, tokens.get(30).getType());
		Assert.assertEquals(Token.TokenType.QUESTIONMARK, tokens.get(31).getType());
		Assert.assertEquals(Token.TokenType.COLON, tokens.get(32).getType());
		Assert.assertEquals(Token.TokenType.MULTIPLY, tokens.get(33).getType());
		Assert.assertEquals(Token.TokenType.DIVIDE, tokens.get(34).getType());
		Assert.assertEquals(Token.TokenType.MODULO, tokens.get(35).getType());
		Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(36).getType());
		Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(37).getType());
		Assert.assertEquals(Token.TokenType.BAR, tokens.get(38).getType());
		Assert.assertEquals(Token.TokenType.COMMA, tokens.get(39).getType());
	}
	
	@Test(expected = InputMismatchException.class)
	public void invalidCharacters()
	{
		// Apostrophe is not a recognized character so lexer throws error
		Lexer lex = new Lexer("$30'\r\n");
		lex.Lex();
	}
	*/
	
}
