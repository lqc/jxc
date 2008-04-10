package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public abstract class Expression extends Instruction {
	
	public static Expression NULL = new NullExpression();
	
	protected Type valueType;
		
	protected Expression(Type t) {
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
