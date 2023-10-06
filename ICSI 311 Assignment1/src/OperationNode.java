import java.util.Optional;

public class OperationNode extends Node
{
	public enum operations {EQ, NE, LT, LE, GT, GE, AND, OR, NOT, 
		MATCH, NOTMATCH, DOLLAR, PREINC, POSTINC, PREDEC, POSTDEC, 
		UNARYPOS, UNARYNEG, IN, EXPONENT, ADD, SUBTRACT, MULTIPLY, 
		DIVIDE, MODULO, CONCATENATION}
	private operations operation;
	private Node left;
	private Optional<Node> right;
	
	public OperationNode(operations operation, Node left)
	{
		this.left = left;
		this.operation = operation;
		right = Optional.empty();
	}
	
	// For when a right is presented.
	public OperationNode(operations operation, Node left, Optional<Node> right)
	{
		this(operation, left);
		this.right = right;
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
	
	public String toString()
	{
		String contents = operation.name();
		
		// Prints left once node type is identified.
		if (left instanceof OperationNode)
		{
			OperationNode opNode = (OperationNode) left;
			opNode.toString();
		}
		
		else if (left instanceof VariableReferenceNode)
		{
			VariableReferenceNode vrNode = (VariableReferenceNode) left;
			vrNode.toString();
		}
		
		else if (left instanceof ConstantNode)
		{
			ConstantNode constNode = (ConstantNode) left;
			constNode.toString();
		}
		
		else if (left instanceof PatternNode)
		{
			PatternNode patNode = (PatternNode) left;
			patNode.toString();
		}
		
		else
		{
			contents += "Unknown type\n";
		}
		
		// If present, prints right once node type is identified.
		if (right.isPresent())
		{
			if (right.get() instanceof OperationNode)
			{
				OperationNode opNode = (OperationNode) right.get();
				opNode.toString();
			}
			
			else if (right.get() instanceof VariableReferenceNode)
			{
				VariableReferenceNode vrNode = (VariableReferenceNode) right.get();
				vrNode.toString();
			}
			
			else if (right.get() instanceof ConstantNode)
			{
				ConstantNode constNode = (ConstantNode) right.get();
				constNode.toString();
			}
			
			else if (right.get() instanceof PatternNode)
			{
				PatternNode patNode = (PatternNode) right.get();
				patNode.toString();
			}
			
			else
			{
				contents += "Unknown type\n";
			}
		}
		
		return contents;
	}
}
