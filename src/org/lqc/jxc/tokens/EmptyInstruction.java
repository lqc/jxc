package org.lqc.jxc.tokens;


public class EmptyInstruction extends Instruction {

	public EmptyInstruction() {
		super(-1, -1);		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

}
