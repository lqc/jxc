package org.lqc.jxc.il;

import org.lqc.jxc.transform.ILVisitor;
import org.lqc.jxc.types.Type;


public class Assignment extends Expression<Type> {
	
	public Assignment(StaticContainer<?> cont, int line, 
			Variable<?> t, Expression<? extends Type> e) {
		super(cont, line, e.type);
		
		target = t;
		argument = e;
	}
	
	protected Variable<?> target;
	protected Expression<? extends Type> argument;
	
	/**
	 * @return the target
	 */
	public Variable<?> getTarget() {
		return target;
	}
	/**
	 * @return the argument
	 */
	public Expression<? extends Type> getArgument() {
		return argument;
	}

	@Override
	public <T> void visit(ILVisitor<T> v) {
		v.process(this);		
	}
}

