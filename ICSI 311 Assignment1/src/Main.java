import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;

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
		
		// Passes file content to Lexer.
		Lexer lex = new Lexer(fileContents);
		// Returns LinkedList of tokens.
		LinkedList<Token> tokens = lex.Lex();
		// Takes in the LinkedList of tokens for parsing.
		Parser pars = new Parser(tokens);
		// Parses the tokens into a program node for interpreter.
	    ProgramNode node = pars.Parse();
	    
	    // Gets an input text file for processing if provided.
	    String textFileName = "String-file.txt";
		Path myTextPath = Paths.get(textFileName);
	    
		// This will process the program node and optional text file.
	    Interpreter interpreter = new Interpreter(node, Optional.of(myTextPath));
	    
	    // This executes the program.
	    interpreter.InterpretProgram();
	}
}
	