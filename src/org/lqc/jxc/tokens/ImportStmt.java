package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.util.PathID;

public final class ImportStmt extends SyntaxTreeNode {
	
	private PathID path;

	public ImportStmt(int line, int column, PathID id) {
		super(line, column);
		
		this.path = id;		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public PathID getPath() {
		return this.path;
	}

}
