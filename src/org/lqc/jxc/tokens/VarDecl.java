package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;

public class VarDecl<T extends Type> extends Declaration<T> {	

	public VarDecl(int l, int c, T t, String id, ExprToken<? extends T> init) {
		super(l, c, t, id);
		initialValue = init;
	}
	
	public VarDecl(T t, String id) {
		super(-1, -1, t, id);
		
		if(t.equals(Type.VOID)) 
			initialValue = ExprToken.VOID(t);
		else
			initialValue = ExprToken.NULL(t);
	}

	protected ExprToken<? extends T> initialValue;
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}

	/**
	 * @return the initialValue
	 */
	public ExprToken<? extends T> getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(ExprToken<? extends T> e) {
		this.initialValue = e;		
	}
	
	
}
