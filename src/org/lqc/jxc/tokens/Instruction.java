package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;

public abstract class Instruction extends SyntaxTreeNode 
{
	protected boolean reachable;
	
	protected Instruction(int l, int c) {
		super(l, c);
		this.reachable = false;
	}

	public static Instruction EMPTY = new EmptyInstruction();

	/**
	 * @return the reachable
	 */
	public boolean isReachable() {
		return reachable;
	}

	/**
	 * @param reachable the reachable to set
	 */
	public void setReachable(boolean reachable) {
		this.reachable = reachable;
	}
	
}
