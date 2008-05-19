package org.lqc.jxc.il;


public class Assignment extends Expression {
	
	public Assignment(StaticContainer cont, int line, 
			Variable t, Expression e) {
		super(cont, line, e.type);
		
		target = t;
		argument = e;
	}
	
	protected Variable target;
	protected Expression argument;
	
	/**
	 * @return the target
	 */
	public Variable getTarget() {
		return target;
	}
	/**
	 * @return the argument
	 */
	public Expression getArgument() {
		return argument;
	}
}
