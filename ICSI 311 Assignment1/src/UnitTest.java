import java.util.InputMismatchException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest 
{	
	@Test
	public void numberTest()
	{
		Lexer lex = new Lexer("5.23  8.5  3 .23\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("NUMBER(5.23) NUMBER(8.5) NUMBER(3) NUMBER(.23) SEPERATOR ", values);
	}
	
	@Test
	public void wordTest()
	{
		Lexer lex = new Lexer("Hello World Hello_World H3110 W0r1D\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(Hello) WORD(World) WORD(Hello_World) WORD(H3110) WORD(W0r1D) SEPERATOR ", values);
	}
	
	@Test
	public void numberAndWordTest()
	{
		Lexer lex = new Lexer("The number 3\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(The) "
				+ "WORD(number) "
				+ "NUMBER(3) "
				+ "SEPERATOR ", values);
	}
	
	@Test
	public void multiLineNumberAndWordTest()
	{
		Lexer lex = new Lexer("The number 7\r\nis better than the number 3\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringValue() + " ";
		}
		
		Assert.assertEquals("WORD(The) "
				+ "WORD(number) "
				+ "NUMBER(7) "
				+ "SEPERATOR "
				+ "WORD(is) "
				+ "WORD(better) "
				+ "WORD(than) "
				+ "WORD(the) "
				+ "WORD(number) "
				+ "NUMBER(3) "
				+ "SEPERATOR ", values);
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
		
		Assert.assertEquals("", values);
	}
	
	@Test
	public void positionTest()
	{
		Lexer lex = new Lexer("A word 123 1\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringPosition() + " ";
		}
		
		Assert.assertEquals("Line Number: 0 Character Position: 0 "
				+ "Line Number: 0 Character Position: 2 "
				+ "Line Number: 0 Character Position: 7 "
				+ "Line Number: 0 Character Position: 11 "
				+ "Line Number: 0 Character Position: 13 ", values);
	}
	
	@Test
	public void multiLinePositionTest()
	{
		Lexer lex = new Lexer("Test 1\r\nTest 1.5\r\nNot a test 1\r\n");
		LinkedList<Token> tokens = lex.Lex();
		String values = "";
		
		for (int i = 0; i < tokens.size(); i++)
		{
			values += tokens.get(i).ToStringPosition() + " ";
		}
		
		Assert.assertEquals("Line Number: 0 Character Position: 0 "
				+ "Line Number: 0 Character Position: 5 "
				+ "Line Number: 0 Character Position: 7 "
				+ "Line Number: 1 Character Position: 0 "
				+ "Line Number: 1 Character Position: 5 "
				+ "Line Number: 1 Character Position: 9 "
				+ "Line Number: 2 Character Position: 0 "
				+ "Line Number: 2 Character Position: 4 "
				+ "Line Number: 2 Character Position: 6 "
				+ "Line Number: 2 Character Position: 11 "
				+ "Line Number: 2 Character Position: 13 ", values);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void wrongNumberTest() 
	{
		Lexer lex = new Lexer("1.4.2\r\n");
		LinkedList<Token> tokens = lex.Lex();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void wrongWordTest()
	{
		Lexer lex = new Lexer("1word\r\n");
		LinkedList<Token> tokens = lex.Lex();
	}
	
	@Test(expected = InputMismatchException.class)
	public void invalidCharacters()
	{
		Lexer lex = new Lexer("$30\r\n");
		LinkedList<Token> tokens = lex.Lex();
	}
	
	
}
