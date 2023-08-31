
public class StringHandler 
{
	private String fileContents;
	private int index;
	
	public StringHandler(String fileContents)
	{
		this.fileContents = fileContents;
		index = 0;
	}

	// looks “i” characters ahead and returns that character; doesn’t move the index
	public char Peek(int i) 
	{
		int num = index + i;
		if (index + i <= fileContents.length() - 1)
			return fileContents.charAt(index + i);
		else
			return '\0';
	}
	
	// returns a string of the next “i” characters but doesn’t move the index
	public String PeekString(int i)
	{
		return fileContents.substring(index, index + i);
	}
	
	// returns the next character and moves the index
	public char GetChar()
	{
		index++;
		return fileContents.charAt(index);
	}
	
	// moves the index ahead “i” positions
	public void Swallow(int i)
	{
		index += i;
	}
	
	// returns true if we are at the end of the document
	public boolean IsDone()
	{
		if (index == (fileContents.length() - 1))
			return true;
		else 
			return false;
	}
	
	// returns the rest of the document as a string

public String Remainder()
	{
		return fileContents.substring(index);
	}
	
	
}
