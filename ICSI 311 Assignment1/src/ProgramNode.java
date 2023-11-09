import java.util.LinkedList;

public class ProgramNode extends Node
{
	private LinkedList<FunctionDefinitionNode> FDN;
	private LinkedList<BlockNode> BeginBlocks;
	private LinkedList<BlockNode> EndBlocks;
	private LinkedList<BlockNode> Blocks;
	
	public ProgramNode()
	{
		this.FDN = new LinkedList<FunctionDefinitionNode>();
		this.BeginBlocks = new LinkedList<BlockNode>();
		this.EndBlocks = new LinkedList<BlockNode>();
		this.Blocks = new LinkedList<BlockNode>();
	}
	
	// Allows access to LinkedLists to be able to manipulate them through LinkedList methods
	public LinkedList<FunctionDefinitionNode> getFunctionDefinitionNode()
	{
		return FDN;
	}
	
	public LinkedList<BlockNode> getBeginBlocks()
	{
		return BeginBlocks;
	}
	
	public LinkedList<BlockNode> getEndBlocks()
	{
		return EndBlocks;
	}
	
	public LinkedList<BlockNode> getBlocks()
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
		
		if(BeginBlocks.isEmpty() == false)
		{
			contents += "Start Blocks:\n";
			for(int i = 0; i < BeginBlocks.size(); i++)
			{
				contents += BeginBlocks.get(i).toString() + "\n";
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
