package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;

public class Branch extends Operation {
	
	public Branch(StaticContainer cont, int line, 
			Expression<? extends Type> c, Operation a, Operation b) {
		super(cont, line);
		
		condition = c;
		opA = a;
		opB = b;
	}

	/** Branch condition. */
	protected Expression<? extends Type> condition;
	
	/** Branch A. */
	protected Operation opA;
	
	/** Branch B. */
	protected Operation opB;

	/**
	 * @return the condition
	 */
	public Expression<? extends Type> getCondition() {
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
	
	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}
