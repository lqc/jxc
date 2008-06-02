package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;

public class CondInstr extends Instruction {
	
	private ExprToken<? extends Type> condition;
	private Instruction trueBlock;
	private Instruction falseBlock;
		
	public CondInstr(int l, int c, ExprToken<? extends Type> e, Instruction i1, Instruction i2) {
		super(l, c);
		condition = e;
		trueBlock = i1;
		falseBlock = i2;		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public ExprToken<? extends Type> getCondition() {
		return this.condition;
	}
	
	public Instruction getBranch(boolean cond) {
		return (cond ? trueBlock : falseBlock);
	}

	public void setCondition(ExprToken<? extends Type> e) {
		this.condition = e;		
	}

}
