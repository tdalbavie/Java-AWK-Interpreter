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
		String fileContent = "";
		Path myPath = Paths.get(fileName);
		
		try 
		{
			fileContent = new String(Files.readAllBytes (myPath));
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Lexer lex = new Lexer(fileContent);
		LinkedList<Token> tokens = lex.Lex();
		
		for (int i = 0; i < tokens.size(); i++)
		{
			tokens.get(i).ToString();
			System.out.print(" ");
		}
	}
}
