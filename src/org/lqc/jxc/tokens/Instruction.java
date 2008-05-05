package org.lqc.jxc.tokens;

import org.lqc.util.TriStateLogic;

public abstract class Instruction extends SyntaxTreeNode 
{
	protected TriStateLogic reachable;
	
	protected Instruction(int l, int c) {
		super(l, c);
		this.reachable = TriStateLogic.UNKNOWN;
	}

	public static Instruction EMPTY = new EmptyInstruction();

	/**
	 * @return the reachable
	 */
	public TriStateLogic isReachable() {
		return reachable;
	}

	/**
	 * @param reachable the reachable to set
	 */
	public void setReachable(TriStateLogic reachable) {
		this.reachable = reachable;
	}
	
}
