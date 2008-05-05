package org.lqc.jxc.il;

public class Call extends Expression 
{
	public Call(StaticContainer cont, Function target)
	{
		super(cont, target.signature.type.getReturnType());
		
		this.target = target;		
		arguments = new Expression[target.signature.type.getArity()];
		last = 0;
	}
	
	protected Function target;	
	protected Expression[] arguments;
	
	private int last;	
	
	public void addArgument(Expression e) {
		arguments[last++] = e;		
	}
	

}
