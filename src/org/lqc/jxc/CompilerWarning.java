package org.lqc.jxc;

import org.lqc.jxc.tokens.SyntaxTreeNode;

public class CompilerWarning  {
	
	public String message;	
	public int line;
	public int column;
	public SyntaxTreeNode source;

	public CompilerWarning(SyntaxTreeNode n) {			
		this.line = n.getLine();
		this.column = n.getColumn();
	}

	public CompilerWarning(SyntaxTreeNode n, String msg) {
		this.source = n;
		this.message = new String(msg);
		this.line = source.getLine();
		this.column = source.getColumn();
	}
	
	public String getMessage() {
		return String.format("[Warning] At line %d: %s",
				line+1, message);
	}

}
