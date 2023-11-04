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
	
	public InterpreterDataType GetIDT(Node node, Optional<HashMap<String, InterpreterDataType>> localVariables)
	{
		// Checks what type of instance node is.
		if (node instanceof AssignmentNode)
		{
			// Create an AssignmentNode so we can more easily work on it.
			AssignmentNode an = (AssignmentNode) node;
			
			if (an.getTarget() instanceof VariableReferenceNode)
			{
				VariableReferenceNode target = (VariableReferenceNode) an.getTarget();
				InterpreterDataType result = GetIDT(an.getExpression(), localVariables);
				
				// If an array index is presented.
				if (target.getIndex().isPresent())
				{
					// Evaluates the index.
					InterpreterDataType index = GetIDT(target.getIndex().get(), localVariables);
					String indexName = index.getType();
					
					// Checks if this array already exists in global variables.
					if (globalVariables.containsKey(target.getName()))
					{
						InterpreterDataType existingIndex = globalVariables.get(target.getName());
						
						if (existingIndex instanceof InterpreterArrayDataType)
						{
							InterpreterArrayDataType indices = (InterpreterArrayDataType) existingIndex;
							indices.getArrayType().put(indexName, result);
							
							return result;
						}
						
						else
							throw new IllegalArgumentException("Cannot assign to an index of a non-array variable.");
						
					}
					
					else if (localVariables.isPresent())
					{
						// Checks if this array already exists in local variables.
						if (localVariables.get().containsKey(target.getName()))
						{
							InterpreterDataType existingIndex = localVariables.get().get(target.getName());
							
							if (existingIndex instanceof InterpreterArrayDataType)
							{
								InterpreterArrayDataType indices = (InterpreterArrayDataType) existingIndex;
								indices.getArrayType().put(indexName, result);
								
								return result;
							}
							
							else
								throw new IllegalArgumentException("Cannot assign to an index of a non-array variable.");
						}
						// Makes a new array in localVariables.
						else
						{
							// Sets up the new array.
							InterpreterArrayDataType indices = new InterpreterArrayDataType();
							// Inputs the result into the provided index name.
							indices.getArrayType().put(indexName, result);
							// Creates a new array and puts it in localVariables.
							localVariables.get().put(target.getName(), indices);
						}
					}
					
					// Makes a new array in globalVariables.
					else
					{
						// Sets up the new array.
						InterpreterArrayDataType indices = new InterpreterArrayDataType();
						// Inputs the result into the provided index name.
						indices.getArrayType().put(indexName, result);
						// Creates a new array and puts it in globalVariables.
						globalVariables.put(target.getName(), indices);
					}
				}
				
				// Assumes that this is not an array.
				else
				{
					// Puts the new assignment into the globalVariables HashMap.
					globalVariables.put(target.getName(), result);
					
					return result;
				}
			}
			
			// Checks for OperationNode and then checks for a field reference operation.
			else if (an.getTarget() instanceof OperationNode)
			{
				OperationNode target = (OperationNode) an.getTarget();
				if (target.getOperation() == OperationNode.operations.DOLLAR)
				{
					InterpreterDataType result = GetIDT(an.getExpression(), localVariables);
					// Gets the expression result in the left node of the field reference OperationNode.
					InterpreterDataType targetExpression = GetIDT(target.getLeftNode(), localVariables);
					
					String targetName = targetExpression.getType();
					
					// Puts the new assignment into the globalVariables HashMap.
					globalVariables.put(targetName, result);
					
					return result;
				}
				
				// Throws an exception when the OperationNode is not a field reference.
				else
					throw new IllegalArgumentException("Assignment must start with a variable or field reference.");
			}
			// Throws an exceptions when the target is something other than a VariableReferenceNode or OperationNode.
			else
				throw new IllegalArgumentException("Assignment must start with a variable or field reference.");
		}
		
		// Returns a new IDT with the value set to the constant node's value.
		if (node instanceof ConstantNode)
		{
			ConstantNode cn = (ConstantNode) node;
			return new InterpreterDataType(cn.getConstantValue());
		}
		
		// Currently not working, this will be fully implemented later.
		if (node instanceof FunctionCallNode)
		{
			FunctionCallNode fcn = (FunctionCallNode) node;
			String FunctionCall = RunFunctionCall(fcn, localVariables.get());
			return new InterpreterDataType(FunctionCall);
		}
		
		// A PatternNode should not be found when passing to a function or assignment, an exception will be thrown.
		if (node instanceof PatternNode)
		{
			throw new IllegalArgumentException("Patterns cannot be used in functions or assignments.");
		}
		
		if (node instanceof TernaryNode)
		{
			TernaryNode ternary = (TernaryNode) node;
			InterpreterDataType bool = GetIDT(ternary.getExpression(), localVariables);
			
			// Checks for false case then evaluates and returns it.
			if (bool.getType().isEmpty() || bool.getType().equals("0"))
			{
				InterpreterDataType falseCase = GetIDT(ternary.getFalseCase(), localVariables);
				return falseCase;
			}
			
			// Evaluates the true case and returns.
			else
			{
				InterpreterDataType trueCase = GetIDT(ternary.getTrueCase(), localVariables);
				return trueCase;
			}
		}
		
		if (node instanceof VariableReferenceNode)
		{
			VariableReferenceNode vrn = (VariableReferenceNode) node;
			// Indices
			if(vrn.getIndex().isPresent())
			{
				String variableName = vrn.getName();
				// Resolves the index from the VariableReferenceNode array variable.
				InterpreterDataType index = GetIDT(vrn.getIndex().get(), localVariables);
				
				if (globalVariables.containsKey(variableName))
				{
					InterpreterDataType indices = globalVariables.get(variableName);
					// Checks if the value attached to the variable is an IADT to indicate an array.
					if (indices instanceof InterpreterArrayDataType)
					{
						// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
						return ((InterpreterArrayDataType) indices).getArrayType().get(index.getType());
					}
					// Throws an exception when the value is found but not an array.
					else
						throw new IllegalArgumentException("Variable being referenced (" + vrn.getName() + ") is not an array.");
				}
				
				if (localVariables.get().containsKey(variableName))
				{
					InterpreterDataType indices = localVariables.get().get(variableName);
					// Checks if the value attached to the variable is an IADT to indicate an array.
					if (indices instanceof InterpreterArrayDataType)
					{
						// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
						return ((InterpreterArrayDataType) indices).getArrayType().get(index.getType());
					}
					// Throws an exception when the value is found but not an array.
					else
						throw new IllegalArgumentException("Variable being referenced (" + vrn.getName() + ") is not an array.");
				}
			}
			
			// If this is not an array reference.
			else
			{
				String variableName = vrn.getName();
				if (globalVariables.containsKey(variableName))
				{
					return globalVariables.get(variableName);
				}
				
				else if (localVariables.isPresent())
				{
					if (localVariables.get().containsKey(variableName))
					{
						return localVariables.get().get(variableName);
					}
					
					// If no instance of this variable is found it will declare a new one with the value 0.
					else
					{
						localVariables.get().put(variableName, new InterpreterDataType("0"));
						return localVariables.get().get(variableName);
					}
				}
				
				// If no instance of this variable is found it will declare a new one with the value 0.
				else
				{
					globalVariables.put(variableName, new InterpreterDataType("0"));
					return globalVariables.get(variableName);
				}
			}
		}
		
		if (node instanceof OperationNode)
		{
			 OperationNode operation = (OperationNode) node;
			 InterpreterDataType left = GetIDT(operation.getLeftNode(), localVariables);
			 
			 // Will continue down this path if there is a right node.
			 if(operation.getRightNode().isPresent())
			 {
				 // This will deal with match.
				 if (operation.getRightNode().get() instanceof PatternNode)
				 {
					 String pattern = ((PatternNode) operation.getRightNode().get()).getPattern();
					 
					 if (operation.getOperation() == OperationNode.operations.MATCH)
					 {
						 String match = left.getType();
						 
						 Pattern regex = Pattern.compile(pattern);
						 Matcher matcher = regex.matcher(match);
						 
						 if(matcher.matches())
						 {
							 
						 }
						 
						 else
						 {
							 
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.NOTMATCH)
					 {
						 
					 }
				 }
				 
				 // This will deal with operations with a left and right node.
				 else
				 {
					 InterpreterDataType right = GetIDT(operation.getRightNode().get(), localVariables);
					 
					 if (operation.getOperation() == OperationNode.operations.EXPONENT)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 // Use Math.pow to get the exponential expression.
						 float result = (float) Math.pow(leftFloat, rightFloat);
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.ADD)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 float result = leftFloat + rightFloat;
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.SUBTRACT)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 float result = leftFloat - rightFloat;
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.MULTIPLY)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 float result = leftFloat * rightFloat;
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.DIVIDE)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 float result = leftFloat / rightFloat;
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.MODULO)
					 {
						 // Converts left and right values to a float.
						 float leftFloat = Float.parseFloat(left.getType());
						 float rightFloat = Float.parseFloat(right.getType());
						 
						 float result = leftFloat % rightFloat;
						 
						 // Returns the result in an IDT.
						 return new InterpreterDataType(Float.toString(result));
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.EQ)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if the floats are equal and returns an IDT with true or false.
							 if (leftFloat == rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 // Checks if the strings are equal and returns an IDT with true or false.
							 if (leftString.equals(rightString))
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.NE)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if the floats are not equal and returns an IDT with true or false.
							 if (leftFloat != rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 // Checks if the strings are not equal and returns an IDT with true or false.
							 if (!leftString.equals(rightString))
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.LT)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if left is less than right and returns an IDT with true or false.
							 if (leftFloat < rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 int stringComparison = leftString.compareTo(rightString);
							 
							 // Checks if left is less than right and returns an IDT with true or false.
							 if (stringComparison < 0)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.LE)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if the floats are equal and returns an IDT with true or false.
							 if (leftFloat <= rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 int stringComparison = leftString.compareTo(rightString);
							 
							 // Checks if left is less than or equal to right and returns an IDT with true or false.
							 if (stringComparison < 0)
								 return new InterpreterDataType("1");
							 
							 // Returns false if leftString is lexicographically smaller than rightString.
							 else if (stringComparison > 0)
								 return new InterpreterDataType("0");
							 
							 // Returns true as by this point they are implied to be equal.
							 else
								 return new InterpreterDataType("1");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.GT)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if left is greater than right and returns an IDT with true or false.
							 if (leftFloat > rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 int stringComparison = leftString.compareTo(rightString);
							 
							 // Checks if left is greater than right and returns an IDT with true or false.
							 if (stringComparison > 0)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.GE)
					 {
						 // Tries to convert left and right to floats.
						 try
						 {
							 // Converts left and right values to a float.
							 float leftFloat = Float.parseFloat(left.getType());
							 float rightFloat = Float.parseFloat(right.getType());
							 
							// Checks if the floats are equal and returns an IDT with true or false.
							 if (leftFloat >= rightFloat)
								 return new InterpreterDataType("1");
							 
							 else
								 return new InterpreterDataType("0");
						 }
						 
						 // If conversions fail it will treat it as a string compare.
						 catch (NumberFormatException e)
						 {
							 // Converts left and right values to a string.
							 String leftString = left.getType();
							 String rightString = right.getType();
							 
							 int stringComparison = leftString.compareTo(rightString);
							 
							 // Checks if left is greater than or equal to right and returns an IDT with true or false.
							 if (stringComparison > 0)
								 return new InterpreterDataType("1");
							 
							 // Returns false if leftString is lexicographically larger than rightString.
							 else if (stringComparison < 0)
								 return new InterpreterDataType("0");
							 
							 // Returns true as by this point they are implied to be equal.
							 else
								 return new InterpreterDataType("1");
						 }
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.AND)
					 {
						 // Converts left and right values to a string.
						 String leftString = left.getType();
						 String rightString = right.getType();
						 
						 // Checks if either left or right string are false (empty string or "0").
						 // Returns false since both values have to be true.
						 if (leftString.isEmpty() || leftString.equals("0") || rightString.isEmpty() || rightString.equals("0"))
							 return new InterpreterDataType("0");
						 
						// Returns true since both values are considered true (not empty string or "0")
						 else
							 return new InterpreterDataType("1");
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.OR)
					 {
						 // Converts left and right values to a string.
						 String leftString = left.getType();
						 String rightString = right.getType();
						 
						 // Checks if either left and right string are false (empty string or "0").
						 // Returns false since neither value is true.
						 if (leftString.isEmpty() || leftString.equals("0") && rightString.isEmpty() || rightString.equals("0"))
							 return new InterpreterDataType("0");
						 
						// Returns true since at least one of the values are considered true (not empty string or "0").
						 else
							 return new InterpreterDataType("1");
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.IN)
					 {
						 if (globalVariables.containsKey(right.getType()))
						 {
							 if (globalVariables.get(right.getType()) instanceof InterpreterArrayDataType)
							 {
								 // Gets all the values of the global array.
								 InterpreterArrayDataType rightArray = (InterpreterArrayDataType) globalVariables.get(right.getType());
								 String leftString = left.getType();
								 
								 // Checks to see if the index exists.
								 if (rightArray.getArrayType().containsKey(leftString))
									 return new InterpreterDataType("1");
								 
								 // If no matches are found, returns false.
								 else
									 return new InterpreterDataType("0");
							 }
							 
							 else
								 throw new IllegalArgumentException("Variable referenced on right side of in is not an array.");
						 }
						 
						 else if (localVariables.isPresent())
						 {
							 if (localVariables.get().containsKey(right.getType()))
							 {
								// Gets all the values of the local array.
								 InterpreterArrayDataType rightArray = (InterpreterArrayDataType) localVariables.get().get(right.getType());
								 String leftString = left.getType();
								 
								 // Checks to see if the index exists.
								 if (rightArray.getArrayType().containsKey(leftString))
									 return new InterpreterDataType("1");
								 
								 // If no matches are found, returns false.
								 else
									 return new InterpreterDataType("0");
							 }
							 
							 else
								 throw new IllegalArgumentException("Right side of in must be a variable reference to an array.");
						 }
						 
						 else
							 throw new IllegalArgumentException("Right side of in must be a variable reference to an array.");
					 }
					 
					 if (operation.getOperation() == OperationNode.operations.CONCATENATION)
					 {
						 // Converts left and right values to a string.
						 String leftString = left.getType();
						 String rightString = right.getType();
						 
						 // Adds the two strings together and returns is in an IDT.
						 return new InterpreterDataType(leftString + rightString);
					 }
				 }
			 
			 }
			 
			 // Goes here if there is only a left node.
			 else
			 {
				 if (operation.getOperation() == OperationNode.operations.PREINC)
				 {
					 // Returns the incremented value.
					 try
					 {
						float floatValue = Float.parseFloat(left.getType());
						floatValue++;
						// Sets the new value of the variable.
						left.setType(Float.toString(floatValue));
						// Returns the new value.
						return new InterpreterDataType(Float.toString(floatValue));
					 }
					 
					 // Throws an exception when trying to increment anything other than a string.
					 catch (NumberFormatException e)
					 {
						 throw new IllegalArgumentException("Can only increment number values.");
					 }
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.POSTINC)
				 {
					 // Returns the incremented value.
					 try
					 {
						float originalFloatValue = Float.parseFloat(left.getType());
						float newFloatValue = originalFloatValue + 1;
						// Sets the new value of the variable.
						left.setType(Float.toString(newFloatValue));
						// Returns the original value since increment happens after the operation.
						return new InterpreterDataType(Float.toString(originalFloatValue));
					 }
					 
					 // Throws an exception when trying to increment anything other than a string.
					 catch (NumberFormatException e)
					 {
						 throw new IllegalArgumentException("Can only increment number values.");
					 }
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.PREDEC)
				 {
					 // Returns the incremented value.
					 try
					 {
						float floatValue = Float.parseFloat(left.getType());
						floatValue--;
						// Sets the new value of the variable.
						left.setType(Float.toString(floatValue));
						// Returns the new value.
						return new InterpreterDataType(Float.toString(floatValue));
					 }
					 
					 // Throws an exception when trying to increment anything other than a string.
					 catch (NumberFormatException e)
					 {
						 throw new IllegalArgumentException("Can only increment number values.");
					 }
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.POSTDEC)
				 {
					 // Returns the incremented value.
					 try
					 {
						float originalFloatValue = Float.parseFloat(left.getType());
						float newFloatValue = originalFloatValue - 1;
						// Sets the new value of the variable.
						left.setType(Float.toString(newFloatValue));
						// Returns the original value since increment happens after the operation.
						return new InterpreterDataType(Float.toString(originalFloatValue));
					 }
					 
					 // Throws an exception when trying to increment anything other than a string.
					 catch (NumberFormatException e)
					 {
						 throw new IllegalArgumentException("Can only increment number values.");
					 }
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.DOLLAR)
				 {
					 try
					 {
						 float leftFloat = Float.parseFloat(left.toString());
						 String variableReferenceName = "$" + leftFloat;
						 
						 // If the field reference already exists it will get the value and return it.
						 if (globalVariables.containsKey(variableReferenceName))
						 {
							 InterpreterDataType value = globalVariables.get(variableReferenceName);
							 return value;
							 
						 }
						 
						 // If the field reference does not exist it will make a new one and return the empty value.
						 else
						 {
							 globalVariables.put(variableReferenceName, new InterpreterDataType(""));
							 InterpreterDataType value = globalVariables.get(variableReferenceName);
							 return value;
						 }
					 }
					 catch (NumberFormatException e)
					 {
						 throw new IllegalArgumentException("Field reference expression must be numerical.");
					 }
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.NOT)
				 {
					 // Converts left value to a string.
					 String leftString = left.getType();
					 
					 // Checks if left is false (empty string or "0").
					 // Returns true since not does the opposite.
					 if (leftString.isEmpty() && leftString.equals("0"))
						 return new InterpreterDataType("1");
					 
					// Returns false since if it evaluates to true it does the opposite.
					 else
						 return new InterpreterDataType("0");
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.UNARYPOS)
				 {
					 return left;
				 }
				 
				 if (operation.getOperation() == OperationNode.operations.UNARYNEG)
				 {
					 // Checks if value is convertible to float.
					 try
					 {
						 String leftString = left.getType();
						 float leftFloat = Float.parseFloat(leftString);
						 leftFloat = leftFloat * -1;
						 return new InterpreterDataType(Float.toString(leftFloat));
					 }
					 // Assumes a non-integer string and simply sets the value to 0.
					 catch (NumberFormatException e)
					 {
						 return new InterpreterDataType("0");
					 }
				 }
			 }
		}
		
		return null;
	}
	
	// Returns an empty string for now, will be implemented later on.
	public String RunFunctionCall(FunctionCallNode fcn, HashMap<String, InterpreterDataType> locals)
	{
		return "";
	}
}
