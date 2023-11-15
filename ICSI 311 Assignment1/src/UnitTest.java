import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{
	// Note: These unit tests will not be testing for returns. 
	// Instead, I will format the output in the console and print the results for each test and state the expected output.
	@Test
	public void conditionalBlockTest()
	{
		Lexer lexer = new Lexer("BEGIN{i=0}\n"
				+ "(i == 1) {print}\n"
				+ "{i++}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    // Gets the input file for processing.
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // Prints out the test it is on.
	    System.out.println("\nConditional Block Test:\n");
	    
	    // This will print only the second line of String-file.txt.
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void userCreatedFunctionTest()
	{
		Lexer lexer = new Lexer("function square(x){\n"
				+ "return x * x\n"
				+ "}\n"
				+ "BEGIN{\n"
				+ "result = square(5)\n"
				+ "print result\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nUser Created Function Test:\n");
	    
	    // This will print the result of the user defined function (should be 25).
	    interpreter.InterpretProgram();
	}
	
	
	@Test
	public void inputProcessingTest()
	{
		Lexer lexer = new Lexer("{print}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    // Gets the input file for processing.
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // Prints out the test it is on.
	    System.out.println("\nInput Processing Test:\n");
	    
	    // Processes each line of the String-file.txt file and prints it.
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void mathAndLogicTest()
	{
		// Tests the order of operations of 6/2*(1+2) and uses an if chain to check the conditions.
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "result = 6/2*(1+2)\n"
				+ "if (result == 1){\n"
				+ "print \"The number is 1.\"\n"
				+ "}\n"
				+ "else if (result == 9){\n"
				+ "print \"The number is 9.\"\n"
				+ "}\n"
				+ "else{\n"
				+ "print \"The number is not 9 or 1.\"\n"
				+ "}\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nMath And Logic Test:\n");
	    
	    // This will enter the second if statement and print "The number is 9.".
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void forLoopTest()
	{
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "for (i = 1; i <= 5; i++){\n"
				+ "print \"Iteration \", i\n"
				+ "}\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nFor Loop Test:\n");
	    
	    // This will print "Iteration 1" to "Iteration 5".
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void forEachLoopTest()
	{
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "fruits[1] = \"Apple\"\n"
				+ "fruits[2] = \"Banana\"\n"
				+ "fruits[3] = \"Cherry\"\n"
				+ "for (index in fruits){\n"
				+ "print \"Fruits at index \", index, \"is \", fruits[index]"
				+ "}\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nFor Each Loop Test:\n");
	    
	    // This will print "Iteration 1" to "Iteration 5".
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void whileLoopTest()
	{
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "i = 1\n"
				+ "while (i <= 5){\n"
				+ "print \"Iteration \", i\n"
				+ "i++\n"
				+ "}\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nWhile Loop Test:\n");
	    
	    // This will print "Iteration 1" to "Iteration 5".
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void doWhileLoopTest()
	{
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "i = 1\n"
				+ "do {\n"
				+ "print \"Iteration \", i\n"
				+ "i++\n"
				+ "} while (i <= 5)\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nDo While Loop Test:\n");
	    
	    // This will print "Iteration 1" to "Iteration 5".
	    interpreter.InterpretProgram();
	}
	
	@Test
	public void variadicPrintfTest()
	{
		Lexer lexer = new Lexer("BEGIN{\n"
				+ "name = \"John\"\n"
				+ "age = 30\n"
				+ "printf \"Hello, my name is %s and I am %d years old.\n\", name, age\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // Prints out the test it is on.
	    System.out.println("\nVariadic Printf Test:\n");
	    
	    // This will print "Hello, my name is John and I am 30 years old.".
	    interpreter.InterpretProgram();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void userCreatedFunctionVariadicTest()
	{
		Lexer lexer = new Lexer("function square(x){\n"
				+ "return x * x\n"
				+ "}\n"
				+ "BEGIN{\n"
				+ "result = square(5, 6)\n"
				+ "print result\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    Interpreter interpreter = new Interpreter(node, Optional.empty());
	    
	    // This will result in an error because the function call has more parameters than the function definition.
	    interpreter.InterpretProgram();
	}
	
	/*
	// Everything past this point is interpreter 3
	@Test
	public void assignmentNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Creates a new variable a and assigns 5 to a (a = 5).
		VariableReferenceNode vrn = new VariableReferenceNode("a");
		ConstantNode cn = new ConstantNode("5");
		AssignmentNode an = new AssignmentNode(vrn, cn);
		
		interpreter.GetIDT(an, Optional.empty());
		
		Assert.assertTrue(interpreter.globalVariables.containsKey("a"));
		Assert.assertEquals(interpreter.globalVariables.get("a").getType(), "5");
		
	}
	
	@Test
	public void constantNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Will return the IDT containing it's value.
		ConstantNode cn = new ConstantNode("Hello");
		InterpreterDataType result = interpreter.GetIDT(cn, Optional.empty());
		
		Assert.assertEquals(result.getType(), "Hello");
	}
	
	@Test
	public void functionCallNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// This will only return an empty string since RunFunctionCall does nothing right now.
		LinkedList<Node> emptyParameters = new LinkedList<Node>();
		// No parameters will be provided since nothing is currently happening.
		FunctionCallNode fcn = new FunctionCallNode("myFunction", emptyParameters);
		
		// FunctionCalls must give a localVariables HashMap since all variables inside functions are local.
		HashMap<String, InterpreterDataType> localVariables = new HashMap<String, InterpreterDataType>();
		
		InterpreterDataType result = interpreter.GetIDT(fcn, Optional.of(localVariables));
		
		Assert.assertEquals(result.getType(), "");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void patternNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Just using a simple string since this is purely for error checking.
		PatternNode pn = new PatternNode("A pattern.");
		
		interpreter.GetIDT(pn, Optional.empty());
	}
	
	@Test
	public void ternaryNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Tests both true and false cases.
		ConstantNode trueBool = new ConstantNode("Not Empty.");
		ConstantNode falseBool = new ConstantNode("");
		ConstantNode trueCase = new ConstantNode("This is true.");
		ConstantNode falseCase = new ConstantNode("This is false.");
		// Puts a true expression in the ternary.
		TernaryNode trueTN = new TernaryNode(trueBool, trueCase, falseCase);
		// Puts a false expression in the ternary.
		TernaryNode falseTN = new TernaryNode(falseBool, trueCase, falseCase);
		
		InterpreterDataType trueIDT = interpreter.GetIDT(trueTN, Optional.empty());
		InterpreterDataType falseIDT = interpreter.GetIDT(falseTN, Optional.empty());
		
		Assert.assertEquals(trueIDT.getType(), "This is true.");
		Assert.assertEquals(falseIDT.getType(), "This is false.");
	}
	
	@Test
	public void variableReferenceNodeTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Scalars are tested in AssignmentNode, I will test array's here.
		ConstantNode indexName = new ConstantNode("apple");
		VariableReferenceNode arrayVRN = new VariableReferenceNode("fruits", Optional.of(indexName));
		
		interpreter.GetIDT(arrayVRN, Optional.empty());
		
		// Creates a second index for this array.
		indexName = new ConstantNode("orange");
		arrayVRN = new VariableReferenceNode("fruits", Optional.of(indexName));
		
		interpreter.GetIDT(arrayVRN, Optional.empty());
		
		// Checks for the existence of the array variable.
		Assert.assertTrue(interpreter.globalVariables.containsKey("fruits"));
		
		InterpreterArrayDataType indices = (InterpreterArrayDataType) interpreter.globalVariables.get("fruits");
		
		// Only checking for existence of new indices as no value would have been assigned to them.
		Assert.assertTrue(indices.getArrayType().containsKey("apple"));
		Assert.assertTrue(indices.getArrayType().containsKey("orange"));
	}
	
	@Test
	public void operationNodeMathTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Does simple math operations on the two numbers.
		ConstantNode num1 = new ConstantNode("10");
		ConstantNode num2 = new ConstantNode("5");
		// Creates an OperationNode for each operation, Strings will have a .0 at the end since it's float converted to string.
		OperationNode Add = new OperationNode(OperationNode.operations.ADD, num1, Optional.of(num2));
		OperationNode Subtract = new OperationNode(OperationNode.operations.SUBTRACT, num1, Optional.of(num2));
		OperationNode Multiply = new OperationNode(OperationNode.operations.MULTIPLY, num1, Optional.of(num2));
		OperationNode Divide = new OperationNode(OperationNode.operations.DIVIDE, num1, Optional.of(num2));
		OperationNode Exponent = new OperationNode(OperationNode.operations.EXPONENT, num1, Optional.of(num2));
		OperationNode Mod = new OperationNode(OperationNode.operations.MODULO, num1, Optional.of(num2));
		
		// Checks for the result of addition.
		InterpreterDataType result = interpreter.GetIDT(Add, Optional.empty());
		Assert.assertEquals(result.getType(), "15.0");
		
		// Checks for the result of subtraction.
		result = interpreter.GetIDT(Subtract, Optional.empty());
		Assert.assertEquals(result.getType(), "5.0");
		
		// Checks for the result of multiplication.
		result = interpreter.GetIDT(Multiply, Optional.empty());
		Assert.assertEquals(result.getType(), "50.0");
		
		// Checks for the result of division.
		result = interpreter.GetIDT(Divide, Optional.empty());
		Assert.assertEquals(result.getType(), "2.0");
		
		// Checks for the result of exponentiation.
		result = interpreter.GetIDT(Exponent, Optional.empty());
		Assert.assertEquals(result.getType(), "100000.0");
		
		// Checks for the result of modulo.
		result = interpreter.GetIDT(Mod, Optional.empty());
		Assert.assertEquals(result.getType(), "0.0");
	}
	
	@Test
	public void operationNodeCompareTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		// Number comparisons.
		ConstantNode num1 = new ConstantNode("10");
		ConstantNode num2 = new ConstantNode("10");
		
		OperationNode numEquals = new OperationNode(OperationNode.operations.EQ, num1, Optional.of(num2));
		OperationNode numNotEquals = new OperationNode(OperationNode.operations.NE, num1, Optional.of(num2));
		OperationNode numLessThan = new OperationNode(OperationNode.operations.LT, num1, Optional.of(num2));
		OperationNode numLessThanOrEquals = new OperationNode(OperationNode.operations.LE, num1, Optional.of(num2));
		OperationNode numGreaterThan = new OperationNode(OperationNode.operations.GT, num1, Optional.of(num2));
		OperationNode numGreaterThanOrEquals = new OperationNode(OperationNode.operations.GE, num1, Optional.of(num2));
		
		// Checks if the numbers are equal.
		result = interpreter.GetIDT(numEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if the numbers are not equal.
		result = interpreter.GetIDT(numNotEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if num1 is less than num2.
		result = interpreter.GetIDT(numLessThan, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if num1 is less than or equal to num2.
		result = interpreter.GetIDT(numLessThanOrEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if num1 is greater than num2.
		result = interpreter.GetIDT(numGreaterThan, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if num1 is greater than or equal to num2.
		result = interpreter.GetIDT(numGreaterThanOrEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// String comparisons.
		ConstantNode str1 = new ConstantNode("string1");
		ConstantNode str2 = new ConstantNode("string2");
		
		OperationNode strEquals = new OperationNode(OperationNode.operations.EQ, str1, Optional.of(str2));
		OperationNode strNotEquals = new OperationNode(OperationNode.operations.NE, str1, Optional.of(str2));
		OperationNode strLessThan = new OperationNode(OperationNode.operations.LT, str1, Optional.of(str2));
		OperationNode strLessThanOrEquals = new OperationNode(OperationNode.operations.LE, str1, Optional.of(str2));
		OperationNode strGreaterThan = new OperationNode(OperationNode.operations.GT, str1, Optional.of(str2));
		OperationNode strGreaterThanOrEquals = new OperationNode(OperationNode.operations.GE, str1, Optional.of(str2));
		
		// Checks if the strings are equal.
		result = interpreter.GetIDT(strEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if the strings are not equal.
		result = interpreter.GetIDT(strNotEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if str1 is less than str2.
		result = interpreter.GetIDT(strLessThan, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if str1 is less than or equal to str2.
		result = interpreter.GetIDT(strLessThanOrEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if str1 is greater than str2.
		result = interpreter.GetIDT(strGreaterThan, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if str1 is greater than or equal to str2.
		result = interpreter.GetIDT(strGreaterThanOrEquals, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
	}
	
	@Test
	public void operationNodeBooleanTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		ConstantNode str1 = new ConstantNode("string");
		ConstantNode str2 = new ConstantNode("");
		
		OperationNode and = new OperationNode(OperationNode.operations.AND, str1, Optional.of(str2));
		OperationNode or = new OperationNode(OperationNode.operations.OR, str1, Optional.of(str2));
		OperationNode not1 = new OperationNode(OperationNode.operations.NOT, str1);
		OperationNode not2 = new OperationNode(OperationNode.operations.NOT, str2);
		
		// Both strings must contain something other than "" or "0".
		result = interpreter.GetIDT(and, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// One string must contain something other than "" or "0".
		result = interpreter.GetIDT(or, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Returns true of it's empty.
		result = interpreter.GetIDT(not1, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Returns true of it's empty.
		result = interpreter.GetIDT(not2, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
	}
	
	@Test
	public void operationNodeMatchTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		PatternNode pattern1 = new PatternNode("fox");
		PatternNode pattern2 = new PatternNode("cat");
		ConstantNode string = new ConstantNode("The quick brown fox jumps over the lazy dog.");
		
		OperationNode match1 = new OperationNode(OperationNode.operations.MATCH, string, Optional.of(pattern1));
		OperationNode notmatch1 = new OperationNode(OperationNode.operations.NOTMATCH, string, Optional.of(pattern1));
		OperationNode match2 = new OperationNode(OperationNode.operations.MATCH, string, Optional.of(pattern2));
		OperationNode notmatch2 = new OperationNode(OperationNode.operations.NOTMATCH, string, Optional.of(pattern2));
		
		// Checks if the pattern "fox" is in the string and returns true.
		result = interpreter.GetIDT(match1, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
		
		// Checks if the pattern "fox" is in the string and return false.
		result = interpreter.GetIDT(notmatch1, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if the pattern "cat" is in the string and return false.
		result = interpreter.GetIDT(match2, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Checks if the pattern "cat" is in the string and returns true.
		result = interpreter.GetIDT(notmatch2, Optional.empty());
		Assert.assertEquals(result.getType(), "1");
	}
	
	@Test
	public void operationNodeDollarTest()
	{
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.of(myPath));
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		// This will generate the fieldReferences for the first line in the file.
		interpreter.lm.SplitAndAssign();
		
		ConstantNode fieldReferenceNum1 = new ConstantNode("5");
		ConstantNode fieldReferenceNum2 = new ConstantNode("100");
		OperationNode fieldReference1 = new OperationNode(OperationNode.operations.DOLLAR, fieldReferenceNum1);
		OperationNode fieldReference2 = new OperationNode(OperationNode.operations.DOLLAR, fieldReferenceNum2);
		
		// This will return the existing IDT at "$5".
		result = interpreter.GetIDT(fieldReference1, Optional.empty());
		Assert.assertEquals(result.getType(), "File");
		
		// This will create a new field reference "$100" and return the new IDT.
		result = interpreter.GetIDT(fieldReference2, Optional.empty());
		Assert.assertEquals(result.getType(), "");
		Assert.assertTrue(interpreter.globalVariables.containsKey("$100"));
	}
	
	@Test
	public void operationNodeIndDecUnaryTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		// Creates a new variable a and assigns 5 to a (a = 5).
		VariableReferenceNode vrn = new VariableReferenceNode("a");
		ConstantNode num = new ConstantNode("5");
		AssignmentNode an = new AssignmentNode(vrn, num);
		interpreter.GetIDT(an, Optional.empty());
		
		// This will be used for Unary Positive.
		ConstantNode str = new ConstantNode("string");
		
		// Uses variable a to change the value inside the globalVariables HashMap.
		OperationNode preinc = new OperationNode(OperationNode.operations.PREINC, vrn);
		OperationNode postinc = new OperationNode(OperationNode.operations.POSTINC, vrn);
		OperationNode predec = new OperationNode(OperationNode.operations.PREDEC, vrn);
		OperationNode postdec = new OperationNode(OperationNode.operations.POSTDEC, vrn);
		OperationNode unarypos = new OperationNode(OperationNode.operations.UNARYPOS, str);
		OperationNode unaryneg = new OperationNode(OperationNode.operations.UNARYNEG, vrn);
		
		// Pre-increment returns updated value.
		result = interpreter.GetIDT(preinc, Optional.empty());
		Assert.assertEquals(result.getType(), "6.0");
		Assert.assertEquals(interpreter.globalVariables.get("a").getType(), "6.0");
		
		// Post-increment returns original value (original value is 6 from previous increment).
		result = interpreter.GetIDT(postinc, Optional.empty());
		Assert.assertEquals(result.getType(), "6.0");
		Assert.assertEquals(interpreter.globalVariables.get("a").getType(), "7.0");
		
		// Pre-decremented returns updated value (reduces to 6 from previous increment).
		result = interpreter.GetIDT(predec, Optional.empty());
		Assert.assertEquals(result.getType(), "6.0");
		Assert.assertEquals(interpreter.globalVariables.get("a").getType(), "6.0");
		
		// Post-increment returns original value (original value is 6 from previous decrement).
		result = interpreter.GetIDT(postdec, Optional.empty());
		Assert.assertEquals(result.getType(), "6.0");
		Assert.assertEquals(interpreter.globalVariables.get("a").getType(), "5.0");
		
		/* 
		 * Unary Positive only force converts a string that could not be converted to float and returns it as 0 to be evaluated as a number.
		 * Normal AWK evaluates comparisons as strings unless force changed by Unary Positive but we do this already in our comparison implementation.
		 * In this case we have a non-integer string so this will return 0.
		 *//*
		result = interpreter.GetIDT(unarypos, Optional.empty());
		Assert.assertEquals(result.getType(), "0");
		
		// Turns the number negative (in this case the value of a which is 5) or string to 0.
		result = interpreter.GetIDT(unaryneg, Optional.empty());
		Assert.assertEquals(result.getType(), "-5.0");
	}
	
	@Test
	public void operationNodeConcatenationTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		// This will hold the result from each operation.
		InterpreterDataType result;
		
		ConstantNode str1 = new ConstantNode("Hello ");
		ConstantNode str2 = new ConstantNode("World!");
		
		OperationNode concat = new OperationNode(OperationNode.operations.CONCATENATION, str1, Optional.of(str2));
		
		// Concatenates the strings.
		result = interpreter.GetIDT(concat, Optional.empty());
		Assert.assertEquals(result.getType(), "Hello World!");
	}
	
	@Test
	public void operationNodeArrayMembershipTest()
	{
		// Creates a dummy interpreter for testing GetIDT.
		Interpreter interpreter = new Interpreter(new ProgramNode(), Optional.empty());
		
		// Scalars are tested in AssignmentNode, I will test array's here.
		ConstantNode indexName = new ConstantNode("apple");
		VariableReferenceNode arrayVRN = new VariableReferenceNode("fruits", Optional.of(indexName));
		
		interpreter.GetIDT(arrayVRN, Optional.empty());
		
		OperationNode trueArrayMembership = new OperationNode(OperationNode.operations.IN, new ConstantNode("apple"), Optional.of(new ConstantNode("fruits")));
		InterpreterDataType trueCase = interpreter.GetIDT(trueArrayMembership, Optional.empty());
		
		OperationNode falesArrayMembership = new OperationNode(OperationNode.operations.IN, new ConstantNode("orange"), Optional.of(new ConstantNode("fruits")));
		InterpreterDataType falseCase = interpreter.GetIDT(falesArrayMembership, Optional.empty());
		
		// "1" is true and "0" is false.
		Assert.assertEquals(trueCase.getType(), "1");
		Assert.assertEquals(falseCase.getType(), "0");
	}
	
	
	// Everything past this point is for Interpreter 1
	@Test
	public void printfTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode printf = (BuiltInFunctionDefinitionNode) interpreter.functions.get("printf");
	    // Testing only for printf and print since no other types are variadic.
	    Assert.assertEquals(printf.isVariadic(), true);
	    
	    // Setup the parameters.
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$4"));
	    parameters.getArrayType().put("1", interpreter.globalVariables.get("$5"));
	    
	    // Setup the formated string.
	    InterpreterDataType formatedString = new InterpreterDataType("This file is a: %s %s\n");
	    
	    // Setup the 
	    InterpreterArrayDataType IADT = new InterpreterArrayDataType();
	    IADT.getArrayType().put("0", formatedString);
	    IADT.getArrayType().put("1", parameters);
	    
	    // Should output: "This file is a: test file"
	    printf.execute(IADT);
	    
	}
	
	@Test
	public void printTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode print = (BuiltInFunctionDefinitionNode) interpreter.functions.get("print");
	    
	    // Testing only for printf and print since no other types are variadic.
	    Assert.assertEquals(print.isVariadic(), true);
	    
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    // Prints the whole line.
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    
	    // Should output: "This is a test file with some text."
	    print.execute(parameters);
	    
	    // Added a space to separate the two prints.
	    System.out.println();
	    
	    // Prints each individual field reference.
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$1"));
	    parameters.getArrayType().put("1", interpreter.globalVariables.get("$2"));
	    parameters.getArrayType().put("2", interpreter.globalVariables.get("$3"));
	    parameters.getArrayType().put("3", interpreter.globalVariables.get("$4"));
	    parameters.getArrayType().put("4", interpreter.globalVariables.get("$5"));
	    
	    // Should output: "Thisisatestfile" (There are no spaces in each field reference so it will be one big block).
	    print.execute(parameters);
	    
	    // Added a space to separate the two prints.
	    System.out.println();
	}
	
	@Test
	public void toupperTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode toupper = (BuiltInFunctionDefinitionNode) interpreter.functions.get("toupper");
	    
	    InterpreterArrayDataType IADT = new InterpreterArrayDataType();
	    IADT.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    
	    Assert.assertEquals(toupper.execute(IADT), "THIS IS A TEST FILE WITH SOME TEXT.");
	}
	
	@Test
	public void tolowerTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode tolower = (BuiltInFunctionDefinitionNode) interpreter.functions.get("tolower");
	    
	    InterpreterArrayDataType IADT = new InterpreterArrayDataType();
	    IADT.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    
	    Assert.assertEquals(tolower.execute(IADT), "this is a test file with some text.");
	}
	
	@Test
	public void substrTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode substr = (BuiltInFunctionDefinitionNode) interpreter.functions.get("substr");
	    
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    parameters.getArrayType().put("1", new InterpreterDataType("9"));
	    
	    Assert.assertEquals(substr.execute(parameters), "A Test File With Some Text.");
	    
	    // Adds the option length parameter.
	    parameters.getArrayType().put("2", new InterpreterDataType("11"));
	    
	    Assert.assertEquals(substr.execute(parameters), "A Test File");
	}
	
	@Test
	public void subTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode sub = (BuiltInFunctionDefinitionNode) interpreter.functions.get("sub");
	    
	    // This will skip to the second line so it can test when a number is encountered.
	    interpreter.lm.SplitAndAssign();

	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", new InterpreterDataType("[0-9]"));
	    parameters.getArrayType().put("1", new InterpreterDataType("5"));
	    
	    sub.execute(parameters);
	    
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "This is some more text with a 5.");
	}
	
	// Split is in an incomplete state and can't save the array but we can still test the return (number of elements created in the array).
	@Test
	public void splitTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode split = (BuiltInFunctionDefinitionNode) interpreter.functions.get("split");
	    
	    // Setup the parameters.
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    // This simulates the array but currently nothing is being done with it so it will be empty.
	    parameters.getArrayType().put("1", new InterpreterDataType()); 
	    
	    Assert.assertEquals(split.execute(parameters), "8");
	}
	
	@Test
	public void matchTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    // This will skip to the second line so it can test when a number is encountered.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode match = (BuiltInFunctionDefinitionNode) interpreter.functions.get("match");
	    
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    parameters.getArrayType().put("1", new InterpreterDataType("[0-9]"));
	    
	    // Finds the number 1 on line 2 of the String-file.txt file.
	    Assert.assertEquals(match.execute(parameters), "31");
	}
	
	@Test
	public void lengthTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode length = (BuiltInFunctionDefinitionNode) interpreter.functions.get("length");
	    
	    InterpreterArrayDataType IADT = new InterpreterArrayDataType();
	    IADT.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    
	    Assert.assertEquals(length.execute(IADT), "35");
	}
	
	@Test
	public void indexTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode index = (BuiltInFunctionDefinitionNode) interpreter.functions.get("index");
	    
	    // Creates the parameters for the index function.
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    parameters.getArrayType().put("0", interpreter.globalVariables.get("$0"));
	    parameters.getArrayType().put("1", new InterpreterDataType("Test"));
	    
	    // Returns the starting character index of the matching string.
	    Assert.assertEquals(index.execute(parameters), "11");
	}
	
	@Test
	public void gsubTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode gsub = (BuiltInFunctionDefinitionNode) interpreter.functions.get("gsub");
	    
	    // This will skip to the second line so it can test when multiple numbers are encountered.
	    interpreter.lm.SplitAndAssign();
	    
	    InterpreterArrayDataType parameters = new InterpreterArrayDataType();
	    
	    // Changes the value of $3 on line 2.
	    Assert.assertEquals(interpreter.globalVariables.get("$3").getType(), "some");
	    parameters.getArrayType().put("0", new InterpreterDataType("some"));
	    parameters.getArrayType().put("1", new InterpreterDataType("a lot"));
	    parameters.getArrayType().put("2", interpreter.globalVariables.get("$3"));
	    Assert.assertEquals(gsub.execute(parameters), "1"); // Returns total substitutions.
	    Assert.assertEquals(interpreter.globalVariables.get("$3").getType(), "a lot");
	    
	    // Gets the third line for work with regex.
	    interpreter.lm.SplitAndAssign();
	    
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "11111");
	    parameters.getArrayType().put("0", new InterpreterDataType("[0-9]"));
	    parameters.getArrayType().put("1", new InterpreterDataType("5"));
	    parameters.getArrayType().remove("2"); // Removes the target parameter from earlier.
	    Assert.assertEquals(gsub.execute(parameters), "5"); // Returns total substitutions.
	    // Replaces 11111 with 55555.
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "55555");
	}
	
	@Test
	public void nextTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode next = (BuiltInFunctionDefinitionNode) interpreter.functions.get("next");
	    
	    // Create an empty IDT since next does not use any parameters.
	    InterpreterDataType empty = new InterpreterDataType();
	    
	    next.execute(empty);
	    
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "This is some more text with a 1.");
	}
	
	// In our implementation, getline will do the same as next.
	@Test
	public void getlineTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    BuiltInFunctionDefinitionNode getline = (BuiltInFunctionDefinitionNode) interpreter.functions.get("getline");
	    
	    // Create an empty IDT since next does not use any parameters.
	    InterpreterDataType empty = new InterpreterDataType();
	    
	    getline.execute(empty);
	    
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "This is some more text with a 1.");
	}
	
	// I added SplitAndAssign into the constructor for now to initialize the field references for the first line.
	// This will likely be changed later but I did it this way so I can easily test if SplitAndAssign works.
	@Test
	public void LineManagerTest()
	{
		// Only using this to initialize interpreter but will not be doing anything with the program node yet.
		Lexer lexer = new Lexer("{break\r\n}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    // Be sure to use provided file to test this.
	    String fileName = "String-file.txt";
		Path myPath = Paths.get(fileName);
	    
	    Interpreter interpreter = new Interpreter(node, Optional.of(myPath));
	    
	    // This will initialize the first line.
	    interpreter.lm.SplitAndAssign();
	    
	    Assert.assertEquals(interpreter.globalVariables.get("$0").getType(), "This Is A Test File With Some Text.");
	    Assert.assertEquals(interpreter.globalVariables.get("$1").getType(), "This");
	    Assert.assertEquals(interpreter.globalVariables.get("$2").getType(), "Is");
	    Assert.assertEquals(interpreter.globalVariables.get("$3").getType(), "A");
	    Assert.assertEquals(interpreter.globalVariables.get("$4").getType(), "Test");
	    Assert.assertEquals(interpreter.globalVariables.get("$5").getType(), "File");
	    Assert.assertEquals(interpreter.globalVariables.get("$6").getType(), "With");
	    Assert.assertEquals(interpreter.globalVariables.get("$7").getType(), "Some");
	    Assert.assertEquals(interpreter.globalVariables.get("$8").getType(), "Text.");
	    Assert.assertEquals(interpreter.globalVariables.get("NF").getType(), "8");
	    Assert.assertEquals(interpreter.globalVariables.get("NR").getType(), "1");
	}
	
	// This tests the changes final changes in the parser, assume parameters are correct.
	@Test
	public void builtInFunctionsTest()
	{
		Lexer lexer = new Lexer("{\r\n"
				+ "    print \"Full line:\", $0\r\n"
				+ "    printf \"Number of fields:\"\r\n"
				+ "    getline\r\n"
				+ "    nextfile\r\n"
				+ "    next\r\n"
				+ "}");
		LinkedList<Token> tokens = lexer.Lex();
	    Parser parser = new Parser(tokens);
	    ProgramNode node = parser.Parse();
	    
	    BlockNode block = node.getBlocks().get(0);
	    FunctionCallNode printFCN = (FunctionCallNode) block.getStatements().get(0);
	    FunctionCallNode printfFCN = (FunctionCallNode) block.getStatements().get(1);
	    FunctionCallNode getlineFCN = (FunctionCallNode) block.getStatements().get(2);
	    FunctionCallNode nextfileFCN = (FunctionCallNode) block.getStatements().get(3);
	    FunctionCallNode nextFCN = (FunctionCallNode) block.getStatements().get(4);
	    
	    Assert.assertEquals(printFCN.getName(), "print");
	    Assert.assertEquals(printfFCN.getName(), "printf");
	    Assert.assertEquals(getlineFCN.getName(), "getline");
	    Assert.assertEquals(nextfileFCN.getName(), "nextfile");
	    Assert.assertEquals(nextFCN.getName(), "next");
	}
	
	
	/*
	// Everything past this point is for Parser 4
	@Test
	public void BreakTest()
	{
		Lexer lexer = new Lexer("{break\r\n}");
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
	    VariableReferenceNode vrNode1 = (VariableReferenceNode) delNode.getIndex().get().get(0);
	    VariableReferenceNode vrNode2 = (VariableReferenceNode) delNode.getIndex().get().get(1);
	    
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
	
	/*
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
