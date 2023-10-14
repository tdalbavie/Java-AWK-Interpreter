
public class WhileNode extends StatementNode
{
	private Node condition;
	private BlockNode statements;
	
	public WhileNode(Node condition, BlockNode statements)
	{
		this.condition = condition;
		this.statements = statements;
	}
	
	public Node getCondition()
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
