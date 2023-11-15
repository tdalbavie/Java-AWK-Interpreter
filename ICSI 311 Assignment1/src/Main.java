// Made by Thomas Dalbavie.

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
		if (args.length == 0)
		{
			System.out.println("Error: Invalid input: Usage: <AWK program file name/path> [<text file name/path>].");
			return;
		}
		
		// Gets the length of the input.
		int inputLength = args.length;
		
		// This will hold the first file name from command line (This is assumed to be the AWK program file).
		String fileName = args[0];
		// String fileName = "file.txt";
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
	    
	    String textFileName;
	    Path myTextPath;
	    Interpreter interpreter;
	    
	    // Checks if the user provided an text file to work on.
		if(inputLength == 2)
		{
		    // Gets an input text file for processing if provided.
		    textFileName = args[1];
			myTextPath = Paths.get(textFileName);
			
			// This will process the program node and optional text file.
		    interpreter = new Interpreter(node, Optional.of(myTextPath));
		}
	    
		else
			interpreter = new Interpreter(node, Optional.empty());
	    
	    // This executes the program.
	    interpreter.InterpretProgram();
	}
}
	