package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class ArgumentDecl extends Declaration {

	public ArgumentDecl(Type t, String id) {
		super(t, id);		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	

}
