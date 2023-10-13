import java.util.Optional;

public class IfNode extends StatementNode
{
	// Will be empty in the case of an else
	Optional<Node> condition;
	BlockNode statements;
	Optional<IfNode> next;
	
	// Only used for else statement.
	public IfNode(BlockNode statements)
	{
		this.statements = statements; 
		condition = Optional.empty();
		next = Optional.empty();
	}
	
	// Used by if/else-if statements.
	public IfNode(Node condition, BlockNode statements)
	{
		this.condition = Optional.of(condition);
		this.statements = statements;
		this.next = Optional.of(null);
	}
	
	public Optional<Node> getCondition()
	{
		return condition;
	}
	
	public BlockNode getStatements()
	{
		return statements;
	}
	
	public Optional<IfNode> getNextIf()
	{
		return next;
	}
	
	// Sets the next IfNode similar to the add method of a linked list.
	public void addIfElse(IfNode next)
	{
		this.next = Optional.of(next);
	}
}
