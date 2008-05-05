package org.lqc.jxc.il;

public class Loop extends Operation {
	
	public Loop(StaticContainer cont, Expression e, Operation body) 
	{
		super(cont);
		this.condition = e;
		this.bodyBlock = body;
	}

	/** Loop continuation condition. */
	protected Expression condition;
		
	/** Loop body. */
	protected Operation bodyBlock;

}
