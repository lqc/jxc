package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;
import org.lqc.jxc.types.Type;

public abstract class Expression extends Instruction {
	
	public static Expression NULL = new NullExpression();
	public static Expression VOID = new VoidExpression();
	
	protected Type valueType;
		
	protected Expression(int l, int c, Type t) {
		super(l, c);
		valueType = t;		
	}
	
	/**
	 * @return the valueType
	 */
	public Type getType() {
		return valueType;
	}
	
	public void setType(Type t) {
		this.valueType = t;
	}
	
}
