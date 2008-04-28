package org.lqc.jxc;

import org.lqc.jxc.tokens.SyntaxTreeNode;
import org.lqc.jxc.types.Type;

@SuppressWarnings("serial")
public class UnmatchedIdentifierException extends SyntaxErrorException {
	
	public UnmatchedIdentifierException(SyntaxTreeNode n, String id, Type t) {
		super(n, "Cannot match '"+id+"' of type '"+t+"' in current context");		
	}
	
	public UnmatchedIdentifierException(SyntaxTreeNode n, String id) {
		this(n, id, Type.ANY);		
	}

}
