package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;
import org.lqc.jxc.types.Type;

public class ArgumentDecl extends Declaration {

	public ArgumentDecl(int l, int c, Type t, String id) {
		super(l, c, t, id);		
	}
	
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	

}
