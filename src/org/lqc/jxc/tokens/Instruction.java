package org.lqc.jxc.tokens;

public abstract class Instruction extends SyntaxTreeNode 
{
	public static Instruction EMPTY = new EmptyInstruction();	
}
