
public class ForNode extends StatementNode
{
	private Node initialization;
	private Node condition;
	private Node increment;
	private BlockNode statements;
	
	public ForNode(Node initialization, Node condition, Node increment, BlockNode statements)
	{
		this.initialization = initialization;
		this.condition = condition;
		this.increment = increment;
		this.statements = statements;
	}
	
	public Node getInitialization()
	{
		return initialization;
	}
	
	public Node getCondition()
	{
		return condition;
	}
	
	public Node getIncrement()
	{
		return increment;
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
