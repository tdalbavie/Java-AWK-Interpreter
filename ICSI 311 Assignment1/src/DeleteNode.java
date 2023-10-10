import java.util.LinkedList;
import java.util.Optional;

public class DeleteNode extends StatementNode
{
	Node ArrayName; // Holds array variable
	Optional<LinkedList<Node>> Index; // Holds list of indices to be deleted if any.
	
	// Used if only a variable is given
	public DeleteNode(Node ArrayName)
	{
		this.ArrayName = ArrayName;
		Index = Optional.empty();
	}
	
	public DeleteNode(Node ArrayName, LinkedList<Node> Index)
	{
		this.ArrayName = ArrayName;
		this.Index = Optional.of(Index);
	}
	
	public Node getArrayName()
	{
		return ArrayName;
	}
	
	public Optional<LinkedList<Node>> getIndex()
	{
		return Index;
	}
}
