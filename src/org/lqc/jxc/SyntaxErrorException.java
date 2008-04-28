package org.lqc.jxc;

import org.lqc.jxc.tokens.SyntaxTreeNode;

public class SyntaxErrorException extends CompilerException {
	
	public int line;
	public int column;

	public SyntaxErrorException(SyntaxTreeNode n) {
		super();		
		this.line = n.getLine();
		this.column = n.getColumn();
	}

	public SyntaxErrorException(SyntaxTreeNode n, String msg) {
		super(msg);
		this.line = n.getLine();
		this.column = n.getColumn();
	}
	
	public String getMessage() {
		return String.format("[Error] At line %d: %s",
				line+1, super.getMessage() );
	}

}
