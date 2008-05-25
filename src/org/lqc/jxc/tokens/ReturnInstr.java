package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;

public class ReturnInstr extends Instruction {
	
	private Expression value;
	
	public ReturnInstr(int l, int c, Expression e) {
		super(l, c);		
		this.value = e;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public Expression getValue() {
		return value;
	}

	public void setValue(Expression e) {
		this.value = e;		
	}

}
