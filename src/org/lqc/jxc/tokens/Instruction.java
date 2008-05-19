package org.lqc.jxc.tokens;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.lqc.util.TriStateLogic;

public abstract class Instruction 
	extends SyntaxTreeNode implements Iterable<Instruction> 
{
	protected TriStateLogic reachable;
	private Set<Instruction> singleton;
	
	protected Instruction(int l, int c) {
		super(l, c);
		this.reachable = TriStateLogic.UNKNOWN;
		this.singleton = Collections.singleton(this);
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
	
	public Iterator<Instruction> iterator() {				
		return singleton.iterator();
	}	
}
