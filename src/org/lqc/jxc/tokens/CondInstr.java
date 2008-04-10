package org.lqc.jxc.tokens;

public class CondInstr extends Instruction {
	
	private Expression condition;
	private Instruction trueBlock;
	private Instruction falseBlock;
		
	public CondInstr(Expression e, Instruction i1, Instruction i2) {
		condition = e;
		trueBlock = i1;
		falseBlock = i2;		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public Expression getCondition() {
		return this.condition;
	}
	
	public Instruction getBranch(boolean cond) {
		return (cond ? trueBlock : falseBlock);
	}

}
