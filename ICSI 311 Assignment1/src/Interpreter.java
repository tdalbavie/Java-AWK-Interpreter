import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
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
				
				return true;
			}
			return false;
		}
	}
	
	HashMap<String, InterpreterDataType> globalVariables;
	HashMap<String, FunctionDefinitionNode> function;
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
			this.function.put(function.getFunctionName(), function);
		}
		
		
		
	}
}
