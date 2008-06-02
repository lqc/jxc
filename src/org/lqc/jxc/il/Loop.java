package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;

public class Loop extends Operation {
	
	public Loop(StaticContainer cont, int line, Expression e, Operation body) 
	{
		super(cont, line);
		this.condition = e;
		this.bodyBlock = body;
	}

	/** Loop continuation condition. */
	protected Expression condition;
		
	/** Loop body. */
	protected Operation bodyBlock;

	/**
	 * @return the condition
	 */
	public Expression getCondition() {
		return condition;
	}

	/**
	 * @return the bodyBlock
	 */
	public Operation getBodyBlock() {
		return bodyBlock;
	}

	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}
