import java.util.InputMismatchException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{	
	@Test
	public void numberTest()
	{
		Lexer lex = new Lexer("5.23  8.5  3 .23");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("NUMBER(5.23) NUMBER(8.5) NUMBER(3) NUMBER(.23) SEPARATOR ", values);
	}
	
	@Test
	public void wordTest()
	{
		Lexer lex = new Lexer("Hello World Hello_World H3110 W0r1D");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(Hello) WORD(World) WORD(Hello_World) WORD(H3110) WORD(W0r1D) SEPARATOR ", values);
	}
	
	@Test
	public void numberAndWordTest()
	{
		Lexer lex = new Lexer("The number 3");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(The) "
				+ "WORD(number) "
				+ "NUMBER(3) "
				+ "SEPARATOR ", values);
	}
	
	@Test
	public void multiLineNumberAndWordTest()
	{
		Lexer lex = new Lexer("The number 7\r\nis better than the number 3");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(The) "
				+ "WORD(number) "
				+ "NUMBER(7) "
				+ "SEPARATOR "
				+ "WORD(is) "
				+ "WORD(better) "
				+ "WORD(than) "
				+ "WORD(the) "
				+ "WORD(number) "
				+ "NUMBER(3) "
				+ "SEPARATOR ", values);
	}
	
	@Test
	public void emptyFileTest()
	{
		Lexer lex = new Lexer("");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("SEPARATOR ", values);
	}
	
	@Test
	public void positionTest()
	{
		Lexer lex = new Lexer("A word 123 1");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringPosition() + " ";
		}
		
		Assert.assertEquals("Line Number: 1 Character Position: 1 "
				+ "Line Number: 1 Character Position: 3 "
				+ "Line Number: 1 Character Position: 8 "
				+ "Line Number: 1 Character Position: 12 "
				+ "Line Number: 1 Character Position: 14 ", values);
	}
	
	@Test
	public void multiLinePositionTest()
	{
		Lexer lex = new Lexer("Test 1\r\nTest 1.5\r\nNot a test 1");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringPosition() + " ";
		}
		
		Assert.assertEquals("Line Number: 1 Character Position: 1 "
				+ "Line Number: 1 Character Position: 6 "
				+ "Line Number: 1 Character Position: 8 "
				+ "Line Number: 2 Character Position: 1 "
				+ "Line Number: 2 Character Position: 6 "
				+ "Line Number: 2 Character Position: 10 "
				+ "Line Number: 3 Character Position: 1 "
				+ "Line Number: 3 Character Position: 5 "
				+ "Line Number: 3 Character Position: 7 "
				+ "Line Number: 3 Character Position: 12 "
				+ "Line Number: 3 Character Position: 14 ", values);
	}
	
	@Test
	public void TestFullLine1()
    {
        Lexer lex = new Lexer("$0 = tolower($0)");
        LinkedList<Token> tokens = lex.Lex();
        
        Assert.assertEquals(9, tokens.size());
        Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(0).type);
        Assert.assertEquals(Token.TokenType.NUMBER, tokens.get(1).type);
        Assert.assertEquals(Token.TokenType.ASSIGN, tokens.get(2).type);
        Assert.assertEquals(Token.TokenType.WORD, tokens.get(3).type);
        Assert.assertEquals("tolower", tokens.get(3).value);
        Assert.assertEquals(Token.TokenType.OPENPAREN, tokens.get(4).type);
        Assert.assertEquals(Token.TokenType.DOLLAR, tokens.get(5).type);
        Assert.assertEquals(Token.TokenType.NUMBER, tokens.get(6).type);
        Assert.assertEquals(Token.TokenType.CLOSEPAREN, tokens.get(7).type);
        Assert.assertEquals(Token.TokenType.SEPARATOR, tokens.get(8).type);
    }
	
	@Test(expected = InputMismatchException.class)
	public void invalidCharacters()
	{
		Lexer lex = new Lexer("$30'\r\n");
		LinkedList<Token> tokens = lex.Lex();
	}
	
	
}
