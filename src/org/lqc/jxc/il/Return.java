package org.lqc.jxc.il;

public class Return extends Operation {
	
	public Expression returnValue;

	public Return(StaticContainer cont, int line, Expression e) {
		super(cont, line);
		
		returnValue = e;
	}

}
