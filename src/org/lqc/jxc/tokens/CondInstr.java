package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;

public class CondInstr extends Instruction {
	
	private Expression condition;
	private Instruction trueBlock;
	private Instruction falseBlock;
		
	public CondInstr(int l, int c, Expression e, Instruction i1, Instruction i2) {
		super(l, c);
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
