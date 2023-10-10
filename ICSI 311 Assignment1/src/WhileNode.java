
public class WhileNode extends StatementNode
{
	Node condition;
	BlockNode statements;
	
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
}
