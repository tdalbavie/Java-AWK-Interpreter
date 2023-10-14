
public class ForEachNode extends StatementNode
{
	private Node condition; // Can only contain array membership condition (var in array).
	private BlockNode statements;
	
	public ForEachNode(Node condition, BlockNode statements)
	{
		this.condition = condition;
		this.statements = statements;
	}
	
	public Node getArrayMembershipCondition()
	{
		return condition;
	}
	
	public BlockNode getStatements()
	{
		return statements;
	}
	
	public String toString()
	{
		return null;
	}
}
