
public class ReturnNode extends StatementNode
{
	private Node returnExpression;
	
	public ReturnNode(Node returnExpression)
	{
		this.returnExpression = returnExpression;
	}
	
	public Node getReturnExpression()
	{
		return returnExpression;
	}
	
	public String toString()
	{
		return null;
	}
}
