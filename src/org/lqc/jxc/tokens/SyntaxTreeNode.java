package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.Context;
import org.lqc.jxc.transform.TreeVisitor;

public abstract class SyntaxTreeNode {
	
	protected Context staticContext;
	
	/* Position of this node. */
	protected int line;
	protected int column;

	protected SyntaxTreeNode(int line, int column) {
		this.staticContext = null;	
		this.line = line; 
		this.column = column;
	}
	
	public abstract void visitNode(TreeVisitor v);

	public Context getStaticContext() {
		return this.staticContext;		
	}

	public void bindStaticContext(Context ctx) {
		this.staticContext = ctx;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}
}
