package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;


public class EmptyInstruction extends Instruction {

	public EmptyInstruction() {
		super(-1, -1);		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

}
