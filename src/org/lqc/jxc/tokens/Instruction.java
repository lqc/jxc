package org.lqc.jxc.tokens;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public abstract class Instruction 
	extends SyntaxTreeNode 
		implements Iterable<Instruction>
{
		private Set<Instruction> singleton;
	
	protected Instruction(int l, int c) {
		super(l, c);		
		this.singleton = Collections.singleton(this);
	}

	public static Instruction EMPTY = new EmptyInstruction();
	
	
	public Iterator<Instruction> iterator() {				
		return singleton.iterator();
	}	
}
