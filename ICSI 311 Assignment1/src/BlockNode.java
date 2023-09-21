import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node
{
	private LinkedList<StatementNode> Statements;
	private Optional<Node> Condition;
	
	public BlockNode(LinkedList<StatementNode> Statements, Optional<Node> Condition)
	{
		this.Statements = new LinkedList<StatementNode>(Statements);
		this.Condition = Condition;
	}
	
	public LinkedList<StatementNode> StatementsAccessor()
	{
		return Statements;
	}
	
	public Optional<Node> ConditionAccessor()
	{
		return Condition;
	}
	
	public String toString() 
	{
		return null;
	}
}
