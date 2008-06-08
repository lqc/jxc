package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;

public class Call extends Expression<Type> 
{		
	
	public enum Proto {
		VIRTUAL,
		NONVIRTUAL,
		STATIC,
		CONSTR
	}
		
	public Call(StaticContainer<?> cont, int line, Callable target, 
			Proto p)
	{
		super(cont, line, target.callSignature().type.getReturnType());
		
		this.target = target;		
		arguments = new Expression[target.callSignature().type.getArity()];
		last = 0;
		
		proto = p;
		if( p.equals(Proto.CONSTR) )
			this.type = target.callSignature().type.getArgumentTypes().get(0);
	}	
	
	protected Callable target;	
	protected Expression<? extends Type>[] arguments;
	
	private int last;
	protected Proto proto;
	
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
	
	public Proto protocol() {
		return proto;
	}
}
