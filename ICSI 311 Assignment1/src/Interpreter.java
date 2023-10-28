import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
					globalVariables.put("$" + (i + 1), new InterpreterDataType(words[i]));
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
		globalVariables = new HashMap<String, InterpreterDataType>();
		functions = new HashMap<String, FunctionDefinitionNode>();
		
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
		
		// This will initialize the first line into the globalVariables.
		// This will allow functions like gsub to be used without getting next line.
		lm.SplitAndAssign();
		
		// Iterates through each entry in ProgramNode's FunctionDefinitionNode LinkedList.
		for(FunctionDefinitionNode function : program.getFunctionDefinitionNode())
		{
			// Gets the name as the key of the function and then puts the function as the value.
			this.functions.put(function.getFunctionName(), function);
		}
		
		functions.put("print", new BuiltInFunctionDefinitionNode(true, (printParameters) -> 
		{
			if(printParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) printParameters).getArrayType().size();
				// Checks if parameters are given.
				if(totalEntries != 0)
				{
					// Loops through each entry of parameters and prints it.
					for (int i = 0; i < totalEntries; i++)
					{
						InterpreterDataType value = ((InterpreterArrayDataType) printParameters).getArrayType().get(String.valueOf(i));
						System.out.print(value.getType());
					}
				}
				// This will be run if print has no parameters, assumes $0 (prints the line).
				else
				{
					System.out.print(globalVariables.get("$0").getType());
				}
			}
			else
				throw new IllegalArgumentException("Expected IADT in print statement.");
			return null;
		}));
		
		functions.put("printf", new BuiltInFunctionDefinitionNode(true, (printfParameters) -> 
		{
			if(printfParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) printfParameters).getArrayType().size();
				
				if(totalEntries == 0)
					throw new IllegalArgumentException("printf must have at least a format string.");
				
				// Gets the first string that contains the formatting.
				InterpreterDataType formatedString = ((InterpreterArrayDataType) printfParameters).getArrayType().get("0");
				
				// Gets the parameters.
				InterpreterArrayDataType parameters = (InterpreterArrayDataType) ((InterpreterArrayDataType) printfParameters).getArrayType().get("1");
				// Makes an array of strings to get the values of the parameters.
				String parameterValues[] = new String[parameters.getArrayType().size()];
				for (int i = 0; i < totalEntries; i++)
				{
					parameterValues[i] = parameters.getArrayType().get(String.valueOf(i)).getType();
				}
				// Prints out the formated result.
				System.out.printf(formatedString.getType(), (Object[]) parameterValues);
			}
			else
				throw new IllegalArgumentException("Expected IADT in printf statement.");
			return null;
		}));
		
		functions.put("getline", new BuiltInFunctionDefinitionNode(false, (getLineParameters) -> 
		{
			// Returns 1 if it can successfully get the next line.
			if(lm.SplitAndAssign() == true)
				return "1";
			// Returns 0 if it encounters the end of the file.
			else
				return "0";
		}));
		
		functions.put("next", new BuiltInFunctionDefinitionNode(false, (nextParameters) -> 
		{
			// Returns 1 if it can successfully get the next line.
			if(lm.SplitAndAssign() == true)
				return "1";
			// Returns 0 if it encounters the end of the file.
			else
				return "0";
		}));
		
		functions.put("gsub", new BuiltInFunctionDefinitionNode(false, (gsubParameters) -> 
		{
			if(gsubParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) gsubParameters).getArrayType().size();
				
				if (totalEntries == 0)
					throw new IllegalArgumentException("gsub must contain a regexp and replacement.");
				else if (totalEntries == 1)
					throw new IllegalArgumentException("gsub must contain a replacement string.");
				
				// Checks if there is a third value in the HashMap to indicate a variable replacement.
				if(((InterpreterArrayDataType) gsubParameters).getArrayType().containsKey("2"))
				{
					InterpreterDataType IDT = ((InterpreterArrayDataType) gsubParameters).getArrayType().get("2");
					String IDTtype = IDT.getType();
				
					String regex = ((InterpreterArrayDataType) gsubParameters).getArrayType().get("0").getType();
					String replacement = ((InterpreterArrayDataType) gsubParameters).getArrayType().get("1").getType();
					
					// Takes the regex and replacement string and replaces the contents at that field variable.
					String result = IDTtype.replaceAll(regex, replacement);
					
					// Counts the total number of substitutions made so it can return it.
					int totalSubstitutions = 0;
					int index = result.indexOf(replacement);
					while (index != -1)
					{
						totalSubstitutions++;
						index = result.indexOf(replacement, index + 1);
					}
					
					// Changes the value in the IDT.
					IDT.setType(result);
					
					return Integer.toString(totalSubstitutions);
				}
				// Assumes it will work on $0 (whole line) if no target is given.
				else
				{
					// Gets the string that is held by the field value.
					InterpreterDataType globalIDT = globalVariables.get("$0");
					String globalIDTtype = globalIDT.getType();
					
					String regex = ((InterpreterArrayDataType) gsubParameters).getArrayType().get("0").getType();
					String replacement = ((InterpreterArrayDataType) gsubParameters).getArrayType().get("1").getType();
					
					// Takes the regex and replacement string and replaces the contents at that field variable.
					String result = globalIDTtype.replaceAll(regex, replacement);
					
					// Counts the total number of substitutions made so it can return it.
					int totalSubstitutions = 0;
					int index = result.indexOf(replacement);
					while (index != -1)
					{
						totalSubstitutions++;
						index = result.indexOf(replacement, index + 1);
					}
					
					// Overrides the existing field reference string with the new one.
					globalIDT.setType(result);
					
					return Integer.toString(totalSubstitutions);
				}
			}
			else
				throw new IllegalArgumentException("Expected IADT in gsub statement.");
		}));
		
		functions.put("index", new BuiltInFunctionDefinitionNode(false, (indexParameters) -> 
		{
			if(indexParameters instanceof InterpreterArrayDataType)
			{
				InterpreterDataType IDT = ((InterpreterArrayDataType) indexParameters).getArrayType().get("0");
				
				String in = ((InterpreterArrayDataType) indexParameters).getArrayType().get("0").getType();
				String find = ((InterpreterArrayDataType) indexParameters).getArrayType().get("1").getType();
				
				int position = in.indexOf(find);
				
				if (position != -1)
					return Integer.toString(++position);
				// Returns 0 when no character was found.
				else
					return "0";
			}
			else
				throw new IllegalArgumentException("Expected IADT in index statement.");
		}));
		
		// Simply returns the length of the string.
		functions.put("length", new BuiltInFunctionDefinitionNode(false, (lengthParameters) -> 
		{
			return Integer.toString(lengthParameters.getType().length());
		}));
		
		functions.put("match", new BuiltInFunctionDefinitionNode(false, (matchParameters) -> 
		{
			if(matchParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) matchParameters).getArrayType().size();
				
				if (totalEntries == 0)
					throw new IllegalArgumentException("match must contain a string and regex.");
				else if (totalEntries == 1)
					throw new IllegalArgumentException("match must contain a regex.");
				
				// Gets the string and regex to be used to find the match.
				String mainString = ((InterpreterArrayDataType) matchParameters).getArrayType().get("0").getType();
				String regex = ((InterpreterArrayDataType) matchParameters).getArrayType().get("1").getType();
				
				// Uses Java's pattern and matcher to find a match.
		        Pattern pattern = Pattern.compile(regex);
		        Matcher matcher = pattern.matcher(mainString);

		        int longestStart = -1;
		        
		        // Finds the longest, leftmost substring matched by the regex.
		        while (matcher.find()) 
		        {
		            int start = matcher.start();

		            if (longestStart == -1 || start < longestStart) 
		            {
		                longestStart = start;
		            }
		        }
		        
		        // Returns the index where the substring begins.
		        if (longestStart != -1) 
		        {
		            return Integer.toString(++longestStart); // Increments the count since Java starts at 0
		        } 
		        // Returns 0 if no match was found.
		        else 
		        {
		            return "0";
		        }
				
			}
			else
				throw new IllegalArgumentException("Expected IADT in match statement.");
		}));
		
		// Currently have no way to assign array to anything so it will be left alone.
		functions.put("split", new BuiltInFunctionDefinitionNode(false, (splitParameters) -> 
		{
			if(splitParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) splitParameters).getArrayType().size();
				
				if (totalEntries == 0)
					throw new IllegalArgumentException("split must contain a string and array.");
				else if (totalEntries == 1)
					throw new IllegalArgumentException("split must contain an array.");
				
				// Gets the first parameter.
				String mainString = ((InterpreterArrayDataType) splitParameters).getArrayType().get("0").getType();
				// Checks if a field separator was provided.
				if (((InterpreterArrayDataType) splitParameters).getArrayType().containsKey("2"))
				{
					String separator = ((InterpreterArrayDataType) splitParameters).getArrayType().get("2").getType();
					String pieces[] = mainString.split(separator);
					
					return Integer.toString(pieces.length);
				}
				// Assumes that the separator is a whitespace.
				else
				{
					String pieces[] = mainString.split(" ");
					
					return Integer.toString(pieces.length);
				}
			}
			else
				throw new IllegalArgumentException("Expected IADT in split statement.");
		}));
		
		// Cannot be done in the current state of the interpreter.
		functions.put("sprintf", new BuiltInFunctionDefinitionNode(true, (sprintfParameters) -> 
		{
			return null;
		}));
		
		functions.put("sub", new BuiltInFunctionDefinitionNode(false, (subParameters) -> 
		{
			if(subParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) subParameters).getArrayType().size();
				
				if (totalEntries == 0)
					throw new IllegalArgumentException("sub must contain a regex and replacement.");
				else if (totalEntries == 1)
					throw new IllegalArgumentException("sub must contain a replacement.");
				
				// Checks to see if there is a target string (target should typically be a variable otherwise substitution gets lost).
				if(((InterpreterArrayDataType) subParameters).getArrayType().containsKey("2"))
				{
					InterpreterDataType IDT = ((InterpreterArrayDataType) subParameters).getArrayType().get("2");
					int hasSubbed = 0; // Gets incremented once and returned to identify if a sub happened.
					String target = ((InterpreterArrayDataType) subParameters).getArrayType().get("2").getType();
					String regex = ((InterpreterArrayDataType) subParameters).getArrayType().get("0").getType();
					String replacement = ((InterpreterArrayDataType) subParameters).getArrayType().get("1").getType();
					
					Pattern pattern = Pattern.compile(regex);
			        Matcher matcher = pattern.matcher(target);
			        
			        // Finds the start and end of the match and uses replacement to sub out the part being changed.
			        if(matcher.find())
			        {
			        	int start = matcher.start();
			            int end = matcher.end();
			            
			            target = target.substring(0, start) + replacement + target.substring(end);
			            IDT.setType(target); // Inserts the new string
			            hasSubbed++;
			        }
			        // Returns 0 if nothing was found or 1 if it was found.
			        return Integer.toString(hasSubbed);
				}
				// Will work on $0 (whole line) if no target is found.
				else
				{
					InterpreterDataType IDT = globalVariables.get("$0");
					int hasSubbed = 0; // Gets incremented once and returned to identify if a sub happened.
					String target = globalVariables.get("$0").getType();
					String regex = ((InterpreterArrayDataType) subParameters).getArrayType().get("0").getType();
					String replacement = ((InterpreterArrayDataType) subParameters).getArrayType().get("1").getType();
					
					Pattern pattern = Pattern.compile(regex);
			        Matcher matcher = pattern.matcher(target);
			        
			        // Finds the start and end of the match and uses replacement to sub out the part being changed.
			        if(matcher.find())
			        {
			        	int start = matcher.start();
			            int end = matcher.end();
			            
			            target = target.substring(0, start) + replacement + target.substring(end);
			            IDT.setType(target); // Inserts the new string
			            
			            hasSubbed++;
			        }
			     // Returns 0 if nothing was found or 1 if it was found.
			        return Integer.toString(hasSubbed);
				}
			}
			else
				throw new IllegalArgumentException("Expected IADT in sub statement.");
		}));
		
		functions.put("substr", new BuiltInFunctionDefinitionNode(false, (substrParameters) -> 
		{
			if(substrParameters instanceof InterpreterArrayDataType)
			{
				int totalEntries = ((InterpreterArrayDataType) substrParameters).getArrayType().size();
				
				if (totalEntries == 0)
					throw new IllegalArgumentException("substr must contain a string and starting character number.");
				else if (totalEntries == 1)
					throw new IllegalArgumentException("substr must contain a starting character number.");
				
				// Checks to see if there is an end length.
				if(((InterpreterArrayDataType) substrParameters).getArrayType().containsKey("2"))
				{
					String string = ((InterpreterArrayDataType) substrParameters).getArrayType().get("0").getType();
					// Start subtracted by one since AWK's first character index starts at 1 unlike Java.
					int start = Integer.parseInt(((InterpreterArrayDataType) substrParameters).getArrayType().get("1").getType()) - 1;
					int length = Integer.parseInt(((InterpreterArrayDataType) substrParameters).getArrayType().get("2").getType()) + start;
					
					return string.substring(start, length);
				}
				// If no end length is provided, assume the whole suffix.
				else
				{
					String string = ((InterpreterArrayDataType) substrParameters).getArrayType().get("0").getType();
					// Start subtracted by one since AWK's first character index starts at 1 unlike Java.
					int start = Integer.parseInt(((InterpreterArrayDataType) substrParameters).getArrayType().get("1").getType()) - 1;
					
					return string.substring(start);
				}
			}
			else
				throw new IllegalArgumentException("Expected IADT in substr statement.");
		}));
		
		functions.put("tolower", new BuiltInFunctionDefinitionNode(false, (tolowerParameters) -> 
		{
			return tolowerParameters.getType().toLowerCase();
		}));
		
		functions.put("toupper", new BuiltInFunctionDefinitionNode(false, (toupperParameters) -> 
		{
			return toupperParameters.getType().toUpperCase();
		}));
		
	}
}
