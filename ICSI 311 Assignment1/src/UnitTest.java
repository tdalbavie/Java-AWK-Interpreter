import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{
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
	public void acceptSeperatorsTest()
	{
		Lexer lex = new Lexer("$\n\n\n0 = tolower\n\n\n($\n\n0)");
		LinkedList<Token> tokens = lex.Lex();
        TokenHandler th = new TokenHandler(tokens);
        Parser pars = new Parser(tokens);
        
        Assert.assertEquals(false, pars.AcceptSeperators(th));
        Assert.assertEquals(Token.TokenType.DOLLAR, th.MatchAndRemove(Token.TokenType.DOLLAR).get().getType());
        Assert.assertEquals(true, pars.AcceptSeperators(th));
        Assert.assertEquals(Token.TokenType.NUMBER, th.MatchAndRemove(Token.TokenType.NUMBER).get().getType());
        Assert.assertEquals(Token.TokenType.ASSIGN, th.MatchAndRemove(Token.TokenType.ASSIGN).get().getType());
        Assert.assertEquals(Token.TokenType.WORD, th.MatchAndRemove(Token.TokenType.WORD).get().getType());
        Assert.assertEquals(true, pars.AcceptSeperators(th));
        Assert.assertEquals(Token.TokenType.OPENPAREN, th.MatchAndRemove(Token.TokenType.OPENPAREN).get().getType());
        Assert.assertEquals(Token.TokenType.DOLLAR, th.MatchAndRemove(Token.TokenType.DOLLAR).get().getType());
        Assert.assertEquals(true, pars.AcceptSeperators(th));
        Assert.assertEquals(Token.TokenType.NUMBER, th.MatchAndRemove(Token.TokenType.NUMBER).get().getType());
        Assert.assertEquals(Token.TokenType.CLOSEPAREN, th.MatchAndRemove(Token.TokenType.CLOSEPAREN).get().getType());
        Assert.assertEquals(Token.TokenType.SEPARATOR, th.MatchAndRemove(Token.TokenType.SEPARATOR).get().getType());
        Assert.assertEquals(false, pars.AcceptSeperators(th)); // Should come back false since there are no more tokens.
	}
	
	@Test
	public void parseFunctionTest()
	{
		Lexer lex = new Lexer("function myFunction(a, b)");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.FunctionDefinitionNodeAccessor().getFirst().FunctionNameAccessor());
	    Assert.assertEquals("a", node.FunctionDefinitionNodeAccessor().getFirst().ParameterNamesAccessor().get(0));
	    Assert.assertEquals("b", node.FunctionDefinitionNodeAccessor().getFirst().ParameterNamesAccessor().get(1));
	}
	
	@Test
	public void parseFunctionWithSeperatorsTest()
	{
		Lexer lex = new Lexer("function\n\n\n myFunction\n\n\n(\n\n\na\n\n,\n\n b\n\n\n)\n\n");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    Assert.assertEquals("myFunction", node.FunctionDefinitionNodeAccessor().getFirst().FunctionNameAccessor());
	    Assert.assertEquals("a", node.FunctionDefinitionNodeAccessor().getFirst().ParameterNamesAccessor().get(0));
	    Assert.assertEquals("b", node.FunctionDefinitionNodeAccessor().getFirst().ParameterNamesAccessor().get(1));
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionBEGINTest()
	{
		Lexer lex = new Lexer("BEGIN");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    Optional<Node> Condition = Optional.empty();
	    
	    Assert.assertEquals(Optional.empty(), node.StartBlockAccessor().getFirst().ConditionAccessor());
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionENDTest()
	{
		Lexer lex = new Lexer("END");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    
	    Assert.assertEquals(Optional.empty(), node.EndBlockAccessor().getFirst().ConditionAccessor());
	}
	
	// Testing is limited due to lack of content, can only test if a BlockNode was made and put into correct LinkedList.
	@Test
	public void parseActionOtherTest()
	{
		Lexer lex = new Lexer("a");
		LinkedList<Token> tokens = lex.Lex();
	    Parser pars = new Parser(tokens);
	    ProgramNode node = pars.Parse();
	    
	    
	    Assert.assertEquals(Optional.empty(), node.BlockAccessor().getFirst().ConditionAccessor());
	}
	
	/* commented old test for future reference when testing exceptions.
	@Test(expected = InputMismatchException.class)
	public void invalidCharacters()
	{
		// Apostrophe is not a recognized character so lexer throws error
		Lexer lex = new Lexer("$30'\r\n");
		LinkedList<Token> tokens = lex.Lex();
	}
	*/
	
}
