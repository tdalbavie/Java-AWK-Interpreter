import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter 
{
	class LineManager
	{
		private List<String> input;
		
		private LineManager(List<String> input)
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
	ProgramNode program;
	LineManager lm;
	
	public Interpreter(ProgramNode program, Optional<Path> filePath)
	{
		globalVariables = new HashMap<String, InterpreterDataType>();
		functions = new HashMap<String, FunctionDefinitionNode>();
		
		// Saves the program to get blocks for processing.
		this.program = program;
		
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
						String stringToPrint = value.getType();
						
						// This will try and truncate any numbers that can be represented as a whole number.
						try
						{
							float floatValue = Float.parseFloat(stringToPrint);
							int intValue = (int) floatValue;
							
							if (floatValue == intValue || floatValue == (int) floatValue) 
							{
			                    System.out.print(intValue + " ");
			                } 
							
							else 
			                {
			                    System.out.print(floatValue + " ");
			                }
						}
						
						catch(NumberFormatException e)
						{
							System.out.print(stringToPrint);
						}
					}
					// Goes to a new line for the next output.
					System.out.println();
				}
				// This will be run if print has no parameters, assumes $0 (prints the line) and goes to the next line.
				else
				{
					System.out.print(globalVariables.get("$0").getType() + "\n");
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
				Object parameterValues[] = new Object[parameters.getArrayType().size()];
				
				for (int i = 0; i < totalEntries; i++)
				{
					String stringToPrint = parameters.getArrayType().get(String.valueOf(i)).getType();
					// Add each element to the string in their respective form for formating.
					try
					{
						float floatValue = Float.parseFloat(stringToPrint);
						int intValue = (int) floatValue;
						
						if (floatValue == intValue || floatValue == (int) floatValue) 
						{
							parameterValues[i] = intValue;
		                } 
						
						else 
		                {
							parameterValues[i] = floatValue;
		                }
					}
					
					catch(NumberFormatException e)
					{
						parameterValues[i] = stringToPrint;
					}
				}
				// Prints out the formated result.
				System.out.printf(formatedString.getType(), parameterValues);
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
				// Gets the string to search in.
				String in = ((InterpreterArrayDataType) indexParameters).getArrayType().get("0").getType();
				// Gets the string that will be used to search for a matching occurrence.
				String find = ((InterpreterArrayDataType) indexParameters).getArrayType().get("1").getType();
				
				// Finds the first occurrence of the string if any.
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
			if(lengthParameters instanceof InterpreterArrayDataType)
			{
				InterpreterDataType parameter = ((InterpreterArrayDataType) lengthParameters).getArrayType().get("0");
				return Integer.toString(parameter.getType().length());
			}
			else
				throw new IllegalArgumentException("Expected IADT in match statement.");
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
			if(tolowerParameters instanceof InterpreterArrayDataType)
			{
				InterpreterDataType parameter = ((InterpreterArrayDataType) tolowerParameters).getArrayType().get("0");
				return parameter.getType().toLowerCase();
			}
			else
				throw new IllegalArgumentException("Expected IADT in match statement.");
		}));
		
		functions.put("toupper", new BuiltInFunctionDefinitionNode(false, (toupperParameters) -> 
		{
			if(toupperParameters instanceof InterpreterArrayDataType)
			{
				InterpreterDataType parameter = ((InterpreterArrayDataType) toupperParameters).getArrayType().get("0");
				return parameter.getType().toUpperCase();
			}
			else
				throw new IllegalArgumentException("Expected IADT in match statement.");
		}));
		
	}
	
	// This will run the interpreter.
	public void InterpretProgram()
	{
		for(BlockNode beginBlock : program.getBeginBlocks())
		{
			InterpretBlock(beginBlock);
		}
		
		// This will run all non begin or end blocks for each record of SplitAndAssign.
		while(lm.SplitAndAssign() != false)
		{
			for(BlockNode block : program.getBlocks())
			{
				InterpretBlock(block);
			}
		}
		
		for(BlockNode endBlock : program.getEndBlocks())
		{
			InterpretBlock(endBlock);
		}
	}
	
	private void InterpretBlock(BlockNode block)
	{
		// Checks to see if there is a condition.
		if (block.getCondition().isPresent())
		{
			Node condition = block.getCondition().get();
			// Evaluates the condition of the block.
			InterpreterDataType conditionResult = GetIDT(condition, Optional.empty());
			
			// Checks if the condition is true (not "0"), will not execute if condition is false.
			if (conditionResult.getType().equals("1"))
			{
				// Process all statements in the block.
				for (Node statement : block.getStatements())
				{
					ProcessStatement(statement, Optional.empty());
				}
			}
		}
		
		// This will run the block if no condition is given.
		else
		{
			// Process all statements in the block.
			for (Node statement : block.getStatements())
			{
				ProcessStatement(statement, Optional.empty());
			}
		}
	}
	
	public ReturnType ProcessStatement(Node stmt, Optional<HashMap<String, InterpreterDataType>> localVariables)
	{
		// This is similar to GetIDT's AssignmentNode but returns a ReturnType instance instead.
		if (stmt instanceof AssignmentNode)
		{
			AssignmentNode assignmentStmt = (AssignmentNode) stmt;
			OperationNode operation = null;
			if (assignmentStmt.getExpression() instanceof OperationNode)
				operation = (OperationNode) assignmentStmt.getExpression();
			
			if (assignmentStmt.getTarget() instanceof VariableReferenceNode)
			{
				// Evaluates the target value and get's its IDT then changes the value to the evaluated result.
				InterpreterDataType target = GetIDT(assignmentStmt.getTarget(), localVariables);
				InterpreterDataType result = GetIDT(assignmentStmt.getExpression(), localVariables);
				
				if (operation != null)
				{
					// This handles an edge case where post operations return the previous value before their operation and causes the variable to never increment.
					// These operations already do assignment on their own so I skip the assignment of post operations, pre operations do not matter as they return the new value.
					if (operation.getOperation() != OperationNode.operations.POSTINC && operation.getOperation() != OperationNode.operations.POSTDEC)
					{
						// Sets the value of the target to the result.
						target.setType(result.getType());
					}
				}
				else
				{
					// Sets the value of the target to the result.
					target.setType(result.getType());
				}

				// Returns a new instance of ReturnType with the result of the assignment.
				return new ReturnType(ReturnType.ReturnTypes.NORMAL, Optional.of(target.getType()));
			}
			
			// Checks for OperationNode and then checks for a field reference operation.
			else if (assignmentStmt.getTarget() instanceof OperationNode)
			{
				// Makes sure the OperationNode contains a Dollar operation.
				OperationNode target = (OperationNode) assignmentStmt.getTarget();
				if (target.getOperation() == OperationNode.operations.DOLLAR)
				{
					// Gets the IDT attached to the field reference.
					InterpreterDataType targetValue = GetIDT(target, localVariables);
					InterpreterDataType result = GetIDT(assignmentStmt.getExpression(), localVariables);
					
					// Sets the value of the target to the result.
					if (operation != null)
					{
						// This handles an edge case where post operations return the previous value before their operation and causes the variable to never increment.
						if (operation.getOperation() != OperationNode.operations.POSTINC && operation.getOperation() != OperationNode.operations.POSTDEC)
						{
							// Sets the value of the target to the result.
							targetValue.setType(result.getType());
						}
					}
					else
					{
						// Sets the value of the target to the result.
						targetValue.setType(result.getType());
					}
					
					// Returns a new instance of ReturnType with the result of the assignment.
					return new ReturnType(ReturnType.ReturnTypes.NORMAL, Optional.of(targetValue.getType()));
				}
				
				// Throws an exception when the OperationNode is not a field reference.
				else
					throw new IllegalArgumentException("Assignment must start with a variable or field reference.");
			}
			// Throws an exceptions when the target is something other than a VariableReferenceNode or OperationNode.
			else
				throw new IllegalArgumentException("Assignment must start with a variable or field reference.");
		}
		
		if (stmt instanceof BreakNode)
		{
			return new ReturnType(ReturnType.ReturnTypes.BREAK);
		}
		
		if (stmt instanceof ContinueNode)
		{
			return new ReturnType(ReturnType.ReturnTypes.CONTINUE);
		}
		
		if (stmt instanceof DeleteNode)
		{
			DeleteNode deleteStmt = (DeleteNode) stmt;
			
			// Checks if array exists in localVariables otherwise checks globalVariables.
			if (localVariables.isPresent())
			{
				if (localVariables.get().containsKey(deleteStmt.getArrayName()))
				{
					InterpreterDataType value = localVariables.get().get(deleteStmt.getArrayName());
					
					// If this is not an IADT it will just ignore it and continue to the next process.
					if (value instanceof InterpreterArrayDataType)
					{
						InterpreterArrayDataType arrayValue = (InterpreterArrayDataType) value;
						
						// If the delete statement contains any indices.
						if (deleteStmt.getIndex().isPresent())
						{
							LinkedList<Node> indices = deleteStmt.getIndex().get();
							
							for(Node node : indices)
							{
								// Evaluates the index expression.
								InterpreterDataType index = GetIDT(node, localVariables);
								
								// Deletes each index it finds that need's to be deleted.
								if (arrayValue.getArrayType().containsKey(index.getType()))
								{
									arrayValue.getArrayType().remove(index.getType());
								}
							}
							
							// Returns normal return type but does not return a value.
							return new ReturnType(ReturnType.ReturnTypes.NORMAL);
						}
						
						// Clears all indices in the HashMap using .clear()
						else
						{
							arrayValue.getArrayType().clear();
							// Returns normal return type but does not return a value.
							return new ReturnType(ReturnType.ReturnTypes.NORMAL);
						}
					}
				}
			}
			
			// This will check globalVariables if no localVariables were given.
			if (globalVariables.containsKey(deleteStmt.getArrayName()))
			{
				InterpreterDataType value = globalVariables.get(deleteStmt.getArrayName());
				
				// If this is not an IADT it will just ignore it and continue to the next process.
				if (value instanceof InterpreterArrayDataType)
				{
					InterpreterArrayDataType arrayValue = (InterpreterArrayDataType) value;
					
					// If the delete statement contains any indices.
					if (deleteStmt.getIndex().isPresent())
					{
						LinkedList<Node> indices = deleteStmt.getIndex().get();
						
						for(Node node : indices)
						{
							// Evaluates the index expression.
							InterpreterDataType index = GetIDT(node, localVariables);
							
							// Deletes each index it finds that need's to be deleted.
							if (arrayValue.getArrayType().containsKey(index.getType()))
							{
								arrayValue.getArrayType().remove(index.getType());
							}
						}
						
						// Returns normal return type but does not return a value.
						return new ReturnType(ReturnType.ReturnTypes.NORMAL);
					}
					
					// Clears all indices in the HashMap using .clear()
					else
					{
						arrayValue.getArrayType().clear();
						// Returns normal return type but does not return a value.
						return new ReturnType(ReturnType.ReturnTypes.NORMAL);
					}
				}
			}
		}
		
		if (stmt instanceof DoWhileNode)
		{
			DoWhileNode doWhileStmt = (DoWhileNode) stmt;
			BlockNode statements = doWhileStmt.getStatements();
			ReturnType rt;
			
			// Calls InterpreterListOfStatements until a break is returned.
			do
			{
				rt = InterpretListOfStatements(statements.getStatements(), localVariables);
				
				// Breaks from the loop when a break is encountered.
				if (rt.getTypeReturned() == ReturnType.ReturnTypes.BREAK)
					break;
				// Returns the ReturnType if a return is encountered.
				else if (rt.getTypeReturned() == ReturnType.ReturnTypes.RETURN)
					return rt;
				
			}while(!GetIDT(doWhileStmt.getCondition(), localVariables).getType().equals("0") && !GetIDT(doWhileStmt.getCondition(), localVariables).getType().isEmpty());
			
			// Returns normal and treats it as a successful execution.
			return new ReturnType(ReturnType.ReturnTypes.NORMAL);
		}
		
		if (stmt instanceof ForNode)
		{
			ForNode forStmt = (ForNode) stmt;
			BlockNode statements = forStmt.getStatements();
			// Initializes the variable in the for loops initialization.
			ProcessStatement(forStmt.getInitialization(), localVariables);
			// Initializes the ReturnType for use in the loop.
			ReturnType rt = new ReturnType(ReturnType.ReturnTypes.NORMAL);
			
			// Checks for a break or if the condition is false.
			while(!GetIDT(forStmt.getCondition(), localVariables).getType().equals("0") && !GetIDT(forStmt.getCondition(), localVariables).getType().isEmpty())
			{
				rt = InterpretListOfStatements(statements.getStatements(), localVariables);
				
				// Evaluates the increment/decrement statement (We will not use the return since this is simply for an assign).
				ProcessStatement(forStmt.getIncrement(), localVariables);
				
				// Breaks from the loop when a break is encountered.
				if (rt.getTypeReturned() == ReturnType.ReturnTypes.BREAK)
					break;
				// Returns the ReturnType if a return is encountered.
				else if (rt.getTypeReturned() == ReturnType.ReturnTypes.RETURN)
					return rt;
			}
			
			// Returns normal return type but does not return a value.
			return new ReturnType(ReturnType.ReturnTypes.NORMAL);
		}
		
		if (stmt instanceof ForEachNode)
		{
			ForEachNode forEachStmt = (ForEachNode) stmt;
			BlockNode statements = forEachStmt.getStatements();
			
			if (forEachStmt.getArrayMembershipCondition() instanceof OperationNode)
			{
				OperationNode condition = (OperationNode) forEachStmt.getArrayMembershipCondition();
				
				// Ensures that the left contains a variable.
				if (condition.getLeftNode() instanceof VariableReferenceNode)
				{
					// Makes sure a second condition is provided.
					if (condition.getRightNode().isPresent())
					{
						// Makes sure the second condition is a variable.
						if(condition.getRightNode().get() instanceof VariableReferenceNode)
						{
							// Gets both variables for use in an advanced for loop.
							VariableReferenceNode leftVar = (VariableReferenceNode) condition.getLeftNode();
							VariableReferenceNode rightVar = (VariableReferenceNode) condition.getRightNode().get();
							
							// Checks if the array is present in localVariables.
							if (localVariables.isPresent())
							{
								if (localVariables.get().containsKey(rightVar.getName()))
								{
									InterpreterDataType arrayValue = localVariables.get().get(rightVar.getName());
									// Makes sure that the variable provided is an array.
									if (arrayValue instanceof InterpreterArrayDataType)
									{
										InterpreterArrayDataType indices = (InterpreterArrayDataType) arrayValue;
										// Evaluates the left value and returns its IDT (This will override an existing variable or create a new one).
										InterpreterDataType leftVarValue = GetIDT(leftVar, localVariables);
										
										// Throws an exception if an array is presented as the variable (AWK technically does something with this but I will throw and exception in this case).
										if (leftVarValue instanceof InterpreterArrayDataType)
											throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
										
										// Loops through each entry in the array HashMap.
										for (Map.Entry<String, InterpreterDataType> entry : indices.getArrayType().entrySet())
										{
											// Sets the variable to the key.
											leftVarValue.setType(entry.getKey());
											
											InterpretListOfStatements(statements.getStatements(), localVariables);
										}
										
										// Returns normal return type but does not return a value.
										return new ReturnType(ReturnType.ReturnTypes.NORMAL);
									}
									
									// Throws an exception when the variable provided is not an array.
									else
										throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
								}
							}
							
							// Checks if the array is present in globalVariables.
							if (globalVariables.containsKey(rightVar.getName()))
							{
								InterpreterDataType arrayValue = globalVariables.get(rightVar.getName());
								// Makes sure that the variable provided is an array.
								if (arrayValue instanceof InterpreterArrayDataType)
								{
									InterpreterArrayDataType indices = (InterpreterArrayDataType) arrayValue;
									// Evaluates the left value and returns its IDT (This will override an existing variable or create a new one).
									InterpreterDataType leftVarValue = GetIDT(leftVar, localVariables);
									
									// Throws an exception if an array is presented as the variable (AWK technically does something with this but I will throw and exception in this case).
									if (leftVarValue instanceof InterpreterArrayDataType)
										throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
									
									// Loops through each entry in the array HashMap.
									for (Map.Entry<String, InterpreterDataType> entry : indices.getArrayType().entrySet())
									{
										// Sets the variable to the key.
										leftVarValue.setType(entry.getKey());
										
										InterpretListOfStatements(statements.getStatements(), localVariables);
									}
									
									// Returns normal return type but does not return a value.
									return new ReturnType(ReturnType.ReturnTypes.NORMAL);
								}
								
								// Throws an exception when the variable provided is not an array.
								else
									throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
							}
							
							// When an undeclared array is given, it will just return and skip over this statement.
							return new ReturnType(ReturnType.ReturnTypes.NORMAL);
						}
						
						// Throws an exception when it encounters anything other than a VariableReferenceType. 
						else
							throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
					}
					
					// Throws an exception when it does not encounter anything on the right.
					else
						throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
				}
				
				// Throws an exception when it encounters anything other than a VariableReferenceType.
				else
					throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
			}
			// Throws an exception when it encounters anything other than an "in" operator in the condition.
			else
				throw new IllegalArgumentException("Error: Invalid For-Each loop notation: must be (var in array).");
		}
		
		// This is similar to GetIDT's FunctionCallNode but returns a ReturnType instance instead.
		if (stmt instanceof FunctionCallNode)
		{
			FunctionCallNode fcnStmt = (FunctionCallNode) stmt;
			String FunctionCallResult;
			
			// Checks if there is a local variables list.
			if (localVariables.isPresent())
				FunctionCallResult = RunFunctionCall(fcnStmt, localVariables.get());
			else
				FunctionCallResult = RunFunctionCall(fcnStmt, null);
			
			// Checks for when the result is null like with print and no parameters.
			if (FunctionCallResult != null)
				return new ReturnType(ReturnType.ReturnTypes.NORMAL, Optional.of(FunctionCallResult));
			else
				return new ReturnType(ReturnType.ReturnTypes.NORMAL);
		}
		
		if (stmt instanceof IfNode)
		{
			IfNode ifStmt = (IfNode) stmt;
			// Using a temporary IfNode to walk through the linked list.
			IfNode tempIfNode = ifStmt;
			
			// Loops until no more nodes are found.
			while(tempIfNode != null)
			{
				// Breaks if it finds an else statement (no condition in IfNode is an else)
				if (tempIfNode.getCondition().isEmpty())
					break;
				
				// Computes the condition.
				InterpreterDataType condition = GetIDT(tempIfNode.getCondition().get(), localVariables);
				
				// Breaks from the loop if a true condition is processed.
				if (condition.getType().equals("1"))
					break;
				
				// Breaks from the loop if a non-empty non-zero string is found.
				if (!condition.getType().isEmpty() && !condition.getType().equals("0"))
					break;
				
				tempIfNode = tempIfNode.getNextIf();
			}
			
			// This will return the ReturnType from InterpretListOfStatements
			if (tempIfNode != null)
			{
				return InterpretListOfStatements(tempIfNode.getStatements().getStatements(), localVariables);
			}
			
			// This will return no value if no condition was found by the end of the if chain.
			else
				return new ReturnType(ReturnType.ReturnTypes.NORMAL);
		}
		
		if (stmt instanceof ReturnNode)
		{
			ReturnNode returnStmt = (ReturnNode) stmt;
			
			// Checks if there is a return expression.
			if (returnStmt.getReturnExpression() != null)
			{
				InterpreterDataType value = GetIDT(returnStmt.getReturnExpression(), localVariables);
				return new ReturnType(ReturnType.ReturnTypes.RETURN, Optional.of(value.getType()));
			}
			
			// Treats it as a return with no parameter.
			else
				return new ReturnType(ReturnType.ReturnTypes.RETURN);
		}
		
		// Same as do while uses a while loop instead.
		if (stmt instanceof WhileNode)
		{
			WhileNode whileStmt = (WhileNode) stmt;
			BlockNode statements = whileStmt.getStatements();
			// Initializes the ReturnType for use in the loop.
			ReturnType rt = new ReturnType(ReturnType.ReturnTypes.NORMAL);
			
			// Calls InterpreterListOfStatements until a break is returned.
			while(!GetIDT(whileStmt.getCondition(), localVariables).getType().equals("0") && !GetIDT(whileStmt.getCondition(), localVariables).getType().isEmpty())
			{
				rt = InterpretListOfStatements(statements.getStatements(), localVariables);
				
				// Breaks from the loop when a break is encountered.
				if (rt.getTypeReturned() == ReturnType.ReturnTypes.BREAK)
					break;
				// Returns the ReturnType if a return is encountered.
				else if (rt.getTypeReturned() == ReturnType.ReturnTypes.RETURN)
					return rt;
			}
			
			// Returns normal return type but does not return a value.
			return new ReturnType(ReturnType.ReturnTypes.NORMAL);
		}
		
		// Throws an exception if any other node is encountered and prints out which node it is.
		throw new IllegalArgumentException("Error: Unexpected statement node type: " + stmt.getClass().getName());
	}
	
	
	public InterpreterDataType GetIDT(Node node, Optional<HashMap<String, InterpreterDataType>> localVariables)
	{
		// Checks what type of instance node is.
		if (node instanceof AssignmentNode)
		{
			// Create an AssignmentNode so we can more easily work on it.
			AssignmentNode an = (AssignmentNode) node;
			OperationNode operation = null;
			if (an.getExpression() instanceof OperationNode)
				operation = (OperationNode) an.getExpression();
			
			if (an.getTarget() instanceof VariableReferenceNode)
			{
				// Evaluates the target value and get's its IDT then changes the value to the evaluated result.
				InterpreterDataType target = GetIDT(an.getTarget(), localVariables);
				InterpreterDataType result = GetIDT(an.getExpression(), localVariables);
				
				if (operation != null)
				{
					// This handles an edge case where post operations return the previous value before their operation and causes the variable to never increment.
					if (operation.getOperation() != OperationNode.operations.POSTINC && operation.getOperation() != OperationNode.operations.POSTDEC)
					{
						// Sets the value of the target to the result.
						target.setType(result.getType());
					}
				}
				else
				{
					// Sets the value of the target to the result.
					target.setType(result.getType());
				}
				
				return target;
			}
			
			// Checks for OperationNode and then checks for a field reference operation.
			else if (an.getTarget() instanceof OperationNode)
			{
				// Makes sure the OperationNode contains a Dollar operation.
				OperationNode target = (OperationNode) an.getTarget();
				if (target.getOperation() == OperationNode.operations.DOLLAR)
				{
					// Gets the IDT attached to the field reference.
					InterpreterDataType targetValue = GetIDT(target, localVariables);
					InterpreterDataType result = GetIDT(an.getExpression(), localVariables);
					
					// Sets the value of the target to the result.
					if (operation != null)
					{
						// This handles an edge case where post operations return the previous value before their operation and causes the variable to never increment.
						if (operation.getOperation() != OperationNode.operations.POSTINC && operation.getOperation() != OperationNode.operations.POSTDEC)
						{
							// Sets the value of the target to the result.
							targetValue.setType(result.getType());
						}
					}
					else
					{
						// Sets the value of the target to the result.
						targetValue.setType(result.getType());
					}
					
					return targetValue;
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
			String FunctionCallResult;
			
			// Checks if there is a local variables list.
			if (localVariables.isPresent())
				FunctionCallResult = RunFunctionCall(fcn, localVariables.get());
			else
				FunctionCallResult = RunFunctionCall(fcn, null);
			
			return new InterpreterDataType(FunctionCallResult);

			
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
				
				// Checks if the array already exists in globalVariables.
				if (globalVariables.containsKey(variableName))
				{
					InterpreterDataType indices = globalVariables.get(variableName);
					// Checks if the value attached to the variable is an IADT to indicate an array.
					if (indices instanceof InterpreterArrayDataType)
					{
						InterpreterArrayDataType allIndices = (InterpreterArrayDataType) indices;
						
						// Checks if the index exists.
						if (allIndices.getArrayType().containsKey(index.getType()))
						{
							// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
							return allIndices.getArrayType().get(index.getType());
						}
						
						// Creates a new index and returns it.
						else	
						{
							// Adds a new entry for the new index with an empty value.
							allIndices.getArrayType().put(index.getType(), new InterpreterDataType(""));
							// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
							return allIndices.getArrayType().get(index.getType());
						}
					}
					// Throws an exception when the value is found but is not an array.
					else
						throw new IllegalArgumentException("Variable being referenced (" + vrn.getName() + ") is not an array.");
				}
				
				else if(localVariables.isPresent())
				{
					// Checks if the array already exists in localVariables.
					if (localVariables.get().containsKey(variableName))
					{
						InterpreterDataType indices = localVariables.get().get(variableName);
						// Checks if the value attached to the variable is an IADT to indicate an array.
						if (indices instanceof InterpreterArrayDataType)
						{
							InterpreterArrayDataType allIndices = (InterpreterArrayDataType) indices;
							
							// Checks if the index exists.
							if (allIndices.getArrayType().containsKey(index.getType()))
							{
								// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
								return allIndices.getArrayType().get(index.getType());
							}
							
							// Creates a new index and returns it.
							else	
							{
								// Adds a new entry for the new index with an empty value.
								allIndices.getArrayType().put(index.getType(), new InterpreterDataType(""));
								// Returns the value at the position indicated in the VariableReferenceNode index from that position in the IADT.
								return allIndices.getArrayType().get(index.getType());
							}
						}
						// Throws an exception when the value is found but is not an array.
						else
							throw new IllegalArgumentException("Variable being referenced (" + vrn.getName() + ") is not an array.");
					}
					
					// Creates a new array with the index if no array was found.
					else
					{
						// Create the new array and add the index.
						InterpreterArrayDataType IADT = new InterpreterArrayDataType();
						IADT.getArrayType().put(index.getType(), new InterpreterDataType(""));
						// Adds the new array to the globalVariables.
						localVariables.get().put(variableName, IADT);
						
						// Returns the newly created IDT attached to the index.
						return localVariables.get().get(variableName);
					}
				}
				
				// Creates a new array with the index if no array was found.
				else
				{
					// Create the new array and add the index.
					InterpreterArrayDataType IADT = new InterpreterArrayDataType();
					IADT.getArrayType().put(index.getType(), new InterpreterDataType(""));
					// Adds the new array to the globalVariables.
					globalVariables.put(variableName, IADT);
					
					// Gets the new IDT that was inserted in the IADT for return.
					InterpreterDataType newIndex = IADT.getArrayType().get(index.getType());
					// Returns the newly created IDT attached to the index.
					return newIndex;
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
						 
						 // Returns an IDT with 1 to signify a match.
						 if(matcher.find())
							 return new InterpreterDataType("1");
						 
						 else
							 return new InterpreterDataType("0");
					 }
					 
					 // Does the same as Match but returns false if a match is found.
					 if (operation.getOperation() == OperationNode.operations.NOTMATCH)
					 {
						 String match = left.getType();
						 
						 Pattern regex = Pattern.compile(pattern);
						 Matcher matcher = regex.matcher(match);
						 
						 // Returns an IDT with 1 to signify a match.
						 if(matcher.find())
							 return new InterpreterDataType("0");
						 
						 else
							 return new InterpreterDataType("1");
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
						 // Truncates decimal values and evaluates it as a whole number.
						 int leftFloat = Integer.parseInt(left.getType());
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
					 if (leftString.isEmpty() || leftString.equals("0"))
						 return new InterpreterDataType("1");
					 
					// Returns false since if it evaluates to true it does the opposite.
					 else
						 return new InterpreterDataType("0");
				 }
				 
				 // This only force converts a non-integer string to 0 so it can be evaluated as a number.
				 if (operation.getOperation() == OperationNode.operations.UNARYPOS)
				 {
					// Checks if value is convertible to float.
					 try
					 {
						 String leftString = left.getType();
						 float leftFloat = Float.parseFloat(leftString);
						 return new InterpreterDataType(Float.toString(leftFloat));
					 }
					// Assumes a non-integer string and simply sets the value to 0.
					 catch (NumberFormatException e)
					 {
						 return new InterpreterDataType("0");
					 }
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
		
		// Throws an exception if any other node is encountered and prints out which node it is.
		throw new IllegalArgumentException("Error: Unexpected GetIDT node type: " + node.getClass().getName() + ".");
	}
	
	private ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, Optional<HashMap<String, InterpreterDataType>> locals)
	{
		for (StatementNode statement : statements)
		{
			ReturnType rt = ProcessStatement(statement, locals);
			if (rt.getTypeReturned() != ReturnType.ReturnTypes.NORMAL)
			{
				return rt;
			}
		}
		
		return new ReturnType(ReturnType.ReturnTypes.NORMAL);
	}
	
	// Returns an empty string for now, will be implemented later on.
	private String RunFunctionCall(FunctionCallNode fcn, HashMap<String, InterpreterDataType> locals)
	{
		String FunctionCallName = fcn.getName();
		
		// Checks if the function being called exists
		if (functions.containsKey(FunctionCallName))
		{
			FunctionDefinitionNode fdn = functions.get(FunctionCallName);
			
			/*
			 *  Because of the way I implemented the lambda functions I will pass in an IDT for single parameter built in functions and an IADT for multiple.
			 *  I did it this way since writing the single parameter functions would be easier however it made this part more difficult.
			 *  I did not know at the time how we would pass things in so I have to do it this way because of my design choice.
			 */
			if (fdn instanceof BuiltInFunctionDefinitionNode)
			{
				BuiltInFunctionDefinitionNode builtIn = (BuiltInFunctionDefinitionNode) fdn;
				
				// Creates a new IADT for the lambda functions.
				InterpreterArrayDataType parameters = new InterpreterArrayDataType();
				
				// This will deal with the one case where it requires a non-variadic parameter and a HashMap of variadic parameters (This could be empty).
				if (FunctionCallName.equals("printf"))
				{
					// Resolves the string for printf.
					InterpreterDataType printfString = GetIDT(fcn.getParameters().get(0), Optional.empty());
					InterpreterArrayDataType printfParameters = new InterpreterArrayDataType();
					
					// Processes each parameter in the function call and puts them in a HashMap.
					for (int i = 1; i < fcn.getParameters().size(); i++)
					{
						// This will create custom numbered keys that the lambda functions can process.
						printfParameters.getArrayType().put(Integer.toString(i - 1), GetIDT(fcn.getParameters().get(i), Optional.empty()));
					}
					
					// Inserts the non-variadic and variadic parameters of printf.
					parameters.getArrayType().put("0", printfString);
					parameters.getArrayType().put("1", printfParameters);
				}
				
				else
				{
					// Processes each parameter in the function call and puts them in a HashMap.
					int i = 0;
					for (Node parameter : fcn.getParameters())
					{
						// This will create custom numbered keys that the lambda functions can process.
						parameters.getArrayType().put(Integer.toString(i), GetIDT(parameter, Optional.empty()));
						i++;
					}
				}
				
				// Returns the result of the BuiltIn function's execution.
				return builtIn.execute(parameters);
			}
			
			// This will process all user defined functions
			else
			{
				// Makes sure the correct number of parameters are called in the function call (Cannot be done with BuiltIn functions since some are overloaded).
				if (fdn.getParameterNames().size() != fcn.getParameters().size())
					throw new IllegalArgumentException("Error: Mismatched parameters: The function " + FunctionCallName + " can only have " + fdn.getParameterNames().size() + " parameters.");
				
				locals = new HashMap<String, InterpreterDataType>();
				
				// Processes each parameter in the function call and puts them in a HashMap.
				int i = 0;
				for (String parameterName : fdn.getParameterNames())
				{
					// This will use the user defined parameter names as the keys to the local variables.
					locals.put(parameterName, GetIDT(fcn.getParameters().get(i++), Optional.empty()));
				}
				
				ReturnType rt = InterpretListOfStatements(fdn.getStatements(), Optional.of(locals));
				
				// Throws if a break was found in a function.
				if (rt.getTypeReturned() == ReturnType.ReturnTypes.BREAK)
					throw new IllegalArgumentException("Error: Unexpected break in function: Illegal use of break in " + FunctionCallName + ".");
				
				// Throws if a continue was found in a function.
				else if (rt.getTypeReturned() == ReturnType.ReturnTypes.CONTINUE)
					throw new IllegalArgumentException("Error: Unexpected break in function: Illegal use of break in " + FunctionCallName + ".");
				
				// Returns the value of the return if a value to return is given.
				else if (rt.getTypeReturned() == ReturnType.ReturnTypes.RETURN)
				{
					// Returns the value of the return.
					if (rt.getReturnValue().isPresent())
						return rt.getReturnValue().get();
					// Returns an empty string if return has no value to return.
					else
						return "";
				}
				
				// Returns an empty string if the function executed without interruption (typeReturned will be NORMAL).
				else
					return "";
			}
		}
		
		// Throws in the case of an undefined function.
		else
			throw new IllegalArgumentException("Error: Function not found: The function " + FunctionCallName + " has not been defined, unable to call function.");
	}
}
