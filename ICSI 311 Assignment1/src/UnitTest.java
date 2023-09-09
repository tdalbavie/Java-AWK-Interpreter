import java.util.InputMismatchException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{		
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
		LinkedList<Token> tokens = lex.Lex();
	}
	
	
}
