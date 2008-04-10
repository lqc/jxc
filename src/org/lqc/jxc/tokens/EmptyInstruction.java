package org.lqc.jxc.tokens;

public class EmptyInstruction extends Instruction {

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

}
