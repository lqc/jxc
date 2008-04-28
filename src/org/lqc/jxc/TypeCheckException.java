package org.lqc.jxc;

import org.lqc.jxc.tokens.SyntaxTreeNode;

@SuppressWarnings("serial")
public class TypeCheckException extends SyntaxErrorException {

	public TypeCheckException(SyntaxTreeNode n, String msg) {
		super(n, msg);		
	}

	public TypeCheckException(SyntaxTreeNode n) {
		super(n);		
	}

	
}
