package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;
import org.lqc.jxc.transform.TreeVisitor;

public class ReturnInstr extends Instruction {
	
	private ExprToken value;
	
	public ReturnInstr(int l, int c, ExprToken e) {
		super(l, c);		
		this.value = e;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public ExprToken getValue() {
		return value;
	}

	public void setValue(ExprToken e) {
		this.value = e;		
	}

}
