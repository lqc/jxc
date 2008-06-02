package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;

public class Call extends Expression<Type> 
{		
	public Call(StaticContainer cont, int line, Callable target) {
		this(cont, line, target, false);
	}
	
	public Call(StaticContainer cont, int line, Callable target, boolean s)
	{
		super(cont, line, target.callSignature().type.getReturnType());
		
		this.target = target;		
		arguments = new Expression[target.callSignature().type.getArity()];
		last = 0;
		
		isStatic = s;
	}	
	
	protected Callable target;	
	protected Expression<? extends Type>[] arguments;
	
	private int last;
	protected boolean isStatic;
	
	public void addArgument(Expression<? extends Type> e) {
		arguments[last++] = e;		
	}
	
	public Expression<? extends Type>[] args() {
		return arguments;
	}
	
	public Callable target() {
		return target;
	}
	
	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}
