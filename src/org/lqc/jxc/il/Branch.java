package org.lqc.jxc.il;

public class Branch extends Operation {
	
	public Branch(StaticContainer cont, int line, 
			Expression c, Operation a, Operation b) {
		super(cont, line);
		
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

	/**
	 * @return the condition
	 */
	public Expression getCondition() {
		return condition;
	}

	/**
	 * @return the opA
	 */
	public Operation getOperationA() {
		return opA;
	}

	/**
	 * @return the opB
	 */
	public Operation getOperationB() {
		return opB;
	}	
}
