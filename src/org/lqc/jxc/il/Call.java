package org.lqc.jxc.il;

public class Call extends Expression 
{
	public Call(StaticContainer cont, int line, Callable target)
	{
		super(cont, line, target.callSignature().type.getReturnType());
		
		this.target = target;		
		arguments = new Expression[target.callSignature().type.getArity()];
		last = 0;
	}
	
	protected Callable target;	
	protected Expression[] arguments;
	
	private int last;	
	
	public void addArgument(Expression e) {
		arguments[last++] = e;		
	}
	
	public Expression[] args() {
		return arguments;
	}
	
	public Callable target() {
		return target;
	}
	

}
