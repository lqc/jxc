package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;

public class LoopInstr extends Instruction {
	
	private ExprToken condition;
	private Instruction body;
	private Instruction initial;
	private Instruction post;
	
	public LoopInstr(int l, int c, ExprToken e, Instruction b, Instruction i, Instruction p) {
		super(l, c);
		this.condition = e;
		this.initial = i;
		this.post = p;
		this.body = b;
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

	/**
	 * @return the condition
	 */
	public ExprToken getCondition() {
		return condition;
	}

	/**
	 * @return the body
	 */
	public Instruction getBody() {
		return body;
	}

	/**
	 * @return the initial
	 */
	public Instruction getInitInstr() {
		return initial;
	}

	/**
	 * @return the post
	 */
	public Instruction getPostInstr() {
		return post;
	}

	public void setCondition(ExprToken e) {
		this.condition = e;		
	}

}
