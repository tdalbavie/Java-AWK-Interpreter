import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node
{
	// Will be null if a block is empty.
	private LinkedList<StatementNode> Statements;
	private Optional<Node> Condition;
	
	public BlockNode(Optional<Node> Condition)
	{
		Statements = new LinkedList<StatementNode>();
		this.Condition = Condition;
	}
	
	public LinkedList<StatementNode> getStatements()
	{
		return Statements;
	}
	
	public Optional<Node> getCondition()
	{
		return Condition;
	}
	
	// No good way to print Blocks until Statements have been implemented.
	public String toString() 
	{
		if (Condition.isPresent())
			return "Condition is present";
		else
			return "Condition is empty";
	}
}
