package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

import org.lqc.jxc.Lexem;

public class ComplexInstr extends Instruction {
	
	public ComplexInstr(int l, int c, List<Instruction> ilist) {
		super(l, c);
		this.instructions = ilist;
	}
	
	public ComplexInstr() {
		super(-1, -1);
		this.instructions = new Vector<Instruction>();
	}

	protected List<Instruction> instructions;

	/**
	 * @return the instructions
	 */
	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}
}
