package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class VarDecl extends Declaration {	

	public VarDecl(int l, int c, Type t, String id, Expression init) {
		super(l, c, t, id);
		initialValue = init;
	}
	
	public VarDecl(Type t, String id) {
		super(-1, -1, t, id);
		
		if(t.equals(Type.VOID)) 
			initialValue = Expression.VOID;
		else
			initialValue = Expression.NULL;
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
