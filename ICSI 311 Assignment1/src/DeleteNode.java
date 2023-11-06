import java.util.LinkedList;
import java.util.Optional;

public class DeleteNode extends StatementNode
{
	private String ArrayName;
	private Optional<LinkedList<Node>> Index; // Holds list of indices to be deleted if any.
	
	// Used if only a variable is given
	public DeleteNode(String ArrayName)
	{
		this.ArrayName = ArrayName;
		Index = null;
	}
	
	public DeleteNode(String ArrayName, Optional<LinkedList<Node>> Index)
	{
		this.ArrayName = ArrayName;
		this.Index = Index;
	}
	
	public String getArrayName()
	{
		return ArrayName;
	}
	
	public Optional<LinkedList<Node>> getIndex()
	{
		return Index;
	}
	
	public String toString()
	{
		return null;
	}
}
