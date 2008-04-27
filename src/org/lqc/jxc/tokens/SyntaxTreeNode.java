package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.Context;

public abstract class SyntaxTreeNode {
	
	protected Context staticContext;

	protected SyntaxTreeNode() {
		this.staticContext = null;		
	}
	
	public abstract void visitNode(TreeVisitor v);

	public Context getStaticContext() {
		return this.staticContext;		
	}

	public void bindStaticContext(Context ctx) {
		this.staticContext = ctx;
	}
}
