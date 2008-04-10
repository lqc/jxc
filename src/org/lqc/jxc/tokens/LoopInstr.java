package org.lqc.jxc.tokens;

public class LoopInstr extends Instruction {
	
	private Expression condition;
	private Instruction body;
	private Instruction initial;
	private Instruction post;
	
	public LoopInstr(Expression e, Instruction b, Instruction i, Instruction p) {
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
	public Expression getCondition() {
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

}