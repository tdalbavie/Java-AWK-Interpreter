import java.util.Optional;

public class OperationNode extends Node
{
	public enum operations {EQ, NE, LT, LE, GT, GE, AND, OR, NOT, 
		MATCH, NOTMATCH, DOLLAR, PREINC, POSTINC, PREDEC, POSTDEC, 
		UNARYPOS, UNARYNEG, IN,EXPONENT, ADD, SUBTRACT, MULTIPLY, 
		DIVIDE, MODULO, CONCATENATION}
	private operations operation;
	private Node left;
	private Optional<Node> right;

	
	public OperationNode(Node left, operations operation)
	{
		this.left = left;
		this.operation = operation;
	}
	
	public operations getOperation()
	{
		return operation;
	}
	
	public Node getLeftNode()
	{
		return left;
	}
	
	public Optional<Node> getRightNode()
	{
		return right;
	}
}
