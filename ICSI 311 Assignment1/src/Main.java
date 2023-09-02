import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Main 
{
	public static void main(String args[])
	{
		String fileName = "file.txt";
		String fileContents = "";
		Path myPath = Paths.get(fileName);
		
		try 
		{
			fileContents = new String(Files.readAllBytes (myPath));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Passes file content to Lexer
		Lexer lex = new Lexer(fileContents);
		// Returns LinkedList of tokens
		LinkedList<Token> tokens = lex.Lex();
		
		for (int i = 0; i < tokens.size(); i++)
		{
			System.out.print(tokens.get(i).ToStringPosition() + " ");
		}
	}
}
	