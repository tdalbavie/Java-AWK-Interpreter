// Thomas Dalbavie 8/24/2023

public class StringHandler 
{
	private String document;
	private int index;
	
	// looks “i” characters ahead and returns that character; doesn’t move the index
	public char Peek(int i) 
	{
		return document.charAt(index + i);
	}
	
	// returns a string of the next “i” characters but doesn’t move the index
	public String PeekString(int i)
	{
		return document.substring(index, index + i);
	}
	
	// returns the next character and moves the index
	public char GetChar()
	{
		index++;
		return document.charAt(index);
	}
	
	// moves the index ahead “i” positions
	public void Swallow(int i)
	{
		index += i;
	}
	
	// returns true if we are at the end of the document
	public boolean IsDone()
	{
		if (index == (document.length() - 1))
			return true;
		else 
			return false;
	}
	
	// returns the rest of the document as a string
	public String Remainder()
	{
		return document.substring(index);
	}
	
	
}
