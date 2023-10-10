
public class ReturnNode extends StatementNode
{
	Node returnExpression;
	
	public ReturnNode(Node returnExpression)
	{
		this.returnExpression = returnExpression;
	}
	
	public Node getReturnExpression()
	{
		return returnExpression;
	}
}
