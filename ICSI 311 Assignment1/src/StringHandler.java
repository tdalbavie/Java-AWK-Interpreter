
public class StringHandler 
{
	// Contains the awk file as a String
	private String fileContents;
	// Keeps track of where it is in the string
	private int index;
	
	// Constructor initializes fileContents and index.
	public StringHandler(String fileContents)
	{
		this.fileContents = fileContents;
		index = 0;
	}

	// looks “i” characters ahead and returns that character but doesn’t move the index.
	public char Peek(int i) 
	{
		if (index + i <= fileContents.length() - 1)
			return fileContents.charAt(index + i);
		else
			return '\0';
	}
	
	// returns a string of the next “i” characters but doesn’t move the index.
	public String PeekString(int i)
	{
		return fileContents.substring(index, index + i);
	}
	
	// returns the next character and moves the index.
	public char GetChar()
	{
		char nextChar = fileContents.charAt(index);
		index++;
		return nextChar;
	}
	
	// moves the index ahead “i” positions.
	public void Swallow(int i)
	{
		index += i;
	}
	
	// returns true if we are at the end of the document.
	public boolean IsDone()
	{
		if (index == (fileContents.length()))
			return true;
		else 
			return false;
	}
	
	// returns the rest of the document as a string.
public String Remainder()
	{
		return fileContents.substring(index);
	}
}
