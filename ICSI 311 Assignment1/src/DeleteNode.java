import java.util.LinkedList;
import java.util.Optional;

public class DeleteNode extends StatementNode
{
	private String ArrayName;
	private LinkedList<Node> Index; // Holds list of indices to be deleted if any.
	
	// Used if only a variable is given
	public DeleteNode(String ArrayName)
	{
		this.ArrayName = ArrayName;
		Index = null;
	}
	
	public DeleteNode(String ArrayName, LinkedList<Node> Index)
	{
		this.ArrayName = ArrayName;
		this.Index = Index;
	}
	
	public String getArrayName()
	{
		return ArrayName;
	}
	
	public LinkedList<Node> getIndex()
	{
		return Index;
	}
}
