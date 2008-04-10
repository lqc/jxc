package org.lqc.jxc.tokens;

public class ReturnInstr extends Instruction {
	
	private Expression value;
	
	public ReturnInstr(Expression e) {
		this.value = e;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public Expression getValue() {
		return value;
	}

}
