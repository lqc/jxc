package org.lqc.jxc.tokens;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class InstrBlock extends InstrList {
	
	public InstrBlock(int l, int c, List<Instruction> ilist) {
		super(l, c);
		this.instructions = ilist;
	}
	
	public InstrBlock() {
		super(-1, -1);
		this.instructions = new Vector<Instruction>();
	}

	protected List<Instruction> instructions;
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
	
	public Iterator<Instruction> iterator() {				
		return instructions.iterator();
	}
}
