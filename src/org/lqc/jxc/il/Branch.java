package org.lqc.jxc.il;

public class Branch extends Operation {
	
	public Branch(StaticContainer cont, 
			Expression c, Operation a, Operation b) {
		super(cont);
		
		condition = c;
		opA = a;
		opB = b;
	}

	/** Branch condition. */
	protected Expression condition;
	
	/** Branch A. */
	protected Operation opA;
	
	/** Branch B. */
	protected Operation opB;	
}
