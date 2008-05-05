package org.lqc.jxc.il;


public class Assignment extends Expression {
	
	public Assignment(StaticContainer cont, 
			Variable t, Expression e) {
		super(cont, e.type);
		
		target = t;
		argument = e;
	}
	
	protected Variable target;
	protected Expression argument;
}
