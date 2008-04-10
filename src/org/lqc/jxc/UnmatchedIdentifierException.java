package org.lqc.jxc;

import org.lqc.jxc.types.Type;

@SuppressWarnings("serial")
public class UnmatchedIdentifierException extends Exception {
	
	public UnmatchedIdentifierException(String id, Type t) {
		super("Cannot match '"+id+"' of type '"+t+"' in current context");		
	}
	
	public UnmatchedIdentifierException(String id) {
		this(id, Type.ANY);		
	}

}
