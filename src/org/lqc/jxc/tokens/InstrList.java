package org.lqc.jxc.tokens;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.lqc.jxc.Lexem;

public class InstrList extends Instruction {
	
	public InstrList(int l, int c, List<Instruction> ilist) {
		super(l, c);
		this.instructions = (ilist == null ? 
				new Vector<Instruction>() : ilist);
	}
	
	public InstrList(int l, int c) {
		this(l, c, null);		
	}
	
	public InstrList() {
		super(-1, -1);
		this.instructions = new Vector<Instruction>();
	}

	protected List<Instruction> instructions;
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
	
	public void add(Instruction i) {
		instructions.add(i);
	}
	
	public Iterator<Instruction> iterator() {				
		return instructions.iterator();
	}
}
