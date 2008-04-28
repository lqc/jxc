package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;

public class EmptyInstruction extends Instruction {

	protected EmptyInstruction() {
		super(-1, -1);		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

}
