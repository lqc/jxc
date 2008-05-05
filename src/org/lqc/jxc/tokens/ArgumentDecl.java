package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class ArgumentDecl extends VarDecl {

	public ArgumentDecl(int l, int c, Type t, String id) {
		super(l, c, t, id, Expression.NULL);		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	

}
