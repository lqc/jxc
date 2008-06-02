package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public abstract class ExprToken<T extends Type> extends Instruction {
	
	public static ExprToken<Type> NULL = new NullExpression();
	public static ExprToken<Type> VOID = new VoidExpression();
	
	public static <A extends Type> ExprToken<? extends A> VOID(A type) {
		return (ExprToken<? extends A>)VOID;
	}
	
	public static <A extends Type> ExprToken<? extends A> NULL(A type) {
		return (ExprToken<? extends A>)NULL;
	}
	
	protected T valueType;
		
	protected ExprToken(int l, int c, T t) {
		super(l, c);
		valueType = t;		
	}
	
	/**
	 * @return the valueType
	 */
	public T getType() {
		return valueType;
	}
	
	public void setType(T t) {
		this.valueType = t;
	}
	
}
