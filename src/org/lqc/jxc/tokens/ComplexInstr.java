package org.lqc.jxc.tokens;

import java.util.List;
import java.util.Vector;

public class ComplexInstr extends Instruction {
	
	public ComplexInstr(List<Instruction> ilist) {
		this.instructions = ilist;
	}
	
	public ComplexInstr() {
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
