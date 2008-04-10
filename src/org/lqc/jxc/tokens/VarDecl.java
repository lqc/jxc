package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

import java_cup.runtime.Symbol;

public class VarDecl extends Declaration {

	public VarDecl(Type t, String id, Expression init) {
		super(t, id);
		initialValue = init;
	}
	
	public VarDecl(Type t, String id) {
		this(t, id, new NullExpression()); 
	}

	protected Expression initialValue;
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}

	/**
	 * @return the initialValue
	 */
	public Expression getInitialValue() {
		return initialValue;
	}
}
