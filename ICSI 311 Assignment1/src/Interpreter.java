import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Interpreter 
{
	class LineManager
	{
		List<String> input;
		
		public LineManager(List<String> input)
		{
			this.input = input;
		}
		
		public boolean SplitAndAssign()
		{
			if(!input.isEmpty())
			{
				// Clears the existing field references in the globalVariables HashMap.
				clearFieldReferences();
				
				int numberOfFields = 0;
				String line = input.get(0);
				input.remove(0); // Removes the line to get the next line when this is called again.
				
				globalVariables.put("$0", new InterpreterDataType(line));
				
				// Splits the line into words based on the defined file separator.
				String words[] = line.split(globalVariables.get("FS").getType());
				// For each word it adds a field reference that.
				for(int i = 0; i < words.length; i++)
				{
					globalVariables.put("$" + i + 1, new InterpreterDataType(words[i]));
					numberOfFields++;
				}
				// Adds the numberOfFields count to the NF variable.
				globalVariables.put("NF", new InterpreterDataType(Integer.toString(numberOfFields)));
				
				// Increment the Number of Records for each line that gets processed.
				int lineCounter = Integer.parseInt(globalVariables.get("NR").getType());
				lineCounter++;
				globalVariables.put("NR", new InterpreterDataType(Integer.toString(lineCounter)));
				// FNR will be set the same way as NR as we are most likely not working with more than one file with our implementation.
				globalVariables.put("FNR", new InterpreterDataType(Integer.toString(lineCounter)));
				
				return true;
			}
			return false;
		}
		
		// Helper method that clears all existing field references before the next sentence to prevent lingering fields.
		private void clearFieldReferences()
		{
			Iterator<String> iterator = globalVariables.keySet().iterator();
			
			// Loops through the globalVariables to delete all field references to prime it for the next line.
			while (iterator.hasNext())
			{
				String key = iterator.next();
				// Uses regex to find the pattern "$i" (i is some integer) and removes each key that matches.
				if (key.matches("\\$\\d+"))
					iterator.remove();
			}
		}
	}
	
	HashMap<String, InterpreterDataType> globalVariables;
	HashMap<String, FunctionDefinitionNode> functions;
	LineManager lm;
	
	public Interpreter(ProgramNode program, Optional<Path> filePath)
	{
		List<String> input = new ArrayList<String>();
		// Checks for file path and populates the list with the file's text, if none is found, it will pass the empty ArrayList of strings in.
		if(filePath.isPresent())
		{
			try 
			{
				input = Files.readAllLines(filePath.get());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			// After reading all lines, defines the FILENAME global variable to the file name.
			globalVariables.put("FILENAME", new InterpreterDataType(filePath.get().getFileName().toString()));
		}
		
		lm = new LineManager(input);
		
		globalVariables.put("FS", new InterpreterDataType(" "));
		globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
		globalVariables.put("OFS", new InterpreterDataType(" "));
		globalVariables.put("ORS", new InterpreterDataType("\n"));
		globalVariables.put("NR", new InterpreterDataType("0"));
		globalVariables.put("FNR", new InterpreterDataType("0"));
		
		// Iterates through each entry in ProgramNode's FunctionDefinitionNode LinkedList.
		for(FunctionDefinitionNode function : program.getFunctionDefinitionNode())
		{
			// Gets the name as the key of the function and then puts the function as the value.
			this.functions.put(function.getFunctionName(), function);
		}
		
		// TODO: print, printf, getline, next, gsub, index, length, match, split, sprintf, sub, substr, tolower, toupper.
		functions.put("print", new BuiltInFunctionDefinitionNode(true, (printMap) -> 
		{
			if(printMap instanceof InterpreterArrayDataType)
			{
				printMap = (InterpreterArrayDataType) printMap;
				
			}
			return null;
		}));
		functions.put("printf", null);
		functions.put("getline", null);
		functions.put("next", null);
		functions.put("gsub", null);
		functions.put("index", null);
		functions.put("length", null);
		functions.put("match", null);
		functions.put("split", null);
		functions.put("sprintf", null);
		functions.put("sub", null);
		functions.put("substr", null);
		functions.put("tolower", null);
		functions.put("toupper", null);
		
	}
}
