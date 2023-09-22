import java.util.LinkedList;

public class ProgramNode extends Node
{
	private LinkedList<FunctionDefinitionNode> FDN;
	private LinkedList<BlockNode> StartBlocks;
	private LinkedList<BlockNode> EndBlocks;
	private LinkedList<BlockNode> Blocks;
	
	public ProgramNode()
	{
		this.FDN = new LinkedList<FunctionDefinitionNode>();
		this.StartBlocks = new LinkedList<BlockNode>();
		this.EndBlocks = new LinkedList<BlockNode>();
		this.Blocks = new LinkedList<BlockNode>();
	}
	
	// Allows access to LinkedLists to be able to manipulate them through LinkedList methods
	public LinkedList<FunctionDefinitionNode> FunctionDefinitionNodeAccessor()
	{
		return FDN;
	}
	
	public LinkedList<BlockNode> StartBlockAccessor()
	{
		return StartBlocks;
	}
	
	public LinkedList<BlockNode> EndBlockAccessor()
	{
		return EndBlocks;
	}
	
	public LinkedList<BlockNode> BlockAccessor()
	{
		return Blocks;
	}
	
	// Prints the contents of the class
	public String toString() 
	{
		String contents = "";
		if(FDN.isEmpty() == false)
		{
			contents += "Function Definitions:\n";
			for(int i = 0; i < FDN.size(); i++)
			{
				contents += FDN.get(i).toString() + "\n";
			}
		}
		
		if(StartBlocks.isEmpty() == false)
		{
			contents += "Start Blocks:\n";
			for(int i = 0; i < StartBlocks.size(); i++)
			{
				contents += StartBlocks.get(i).toString() + "\n";
			}
		}
		
		if(EndBlocks.isEmpty() == false)
		{
			contents += "End Blocks:\n";
			for(int i = 0; i < EndBlocks.size(); i++)
			{
				contents += EndBlocks.get(i).toString() + "\n";
			}
		}
		
		if(Blocks.isEmpty() == false)
		{
			contents += "Blocks:\n";
			for(int i = 0; i < Blocks.size(); i++)
			{
				contents += Blocks.get(i).toString() + "\n";
			}
		}
		
		return contents;
	}
	
}
