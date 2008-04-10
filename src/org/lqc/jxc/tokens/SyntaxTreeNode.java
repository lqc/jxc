package org.lqc.jxc.tokens;

public abstract class SyntaxTreeNode {
	
	protected SyntaxTreeNode() {}
	
	public abstract void visitNode(TreeVisitor v);
}
