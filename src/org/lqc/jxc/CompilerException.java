package org.lqc.jxc;

public class CompilerException extends RuntimeException {

	public CompilerException(String arg0, Throwable arg1) {
		super(arg0, arg1);		
	}

	public CompilerException(Throwable arg0) {
		super(arg0);		
	}

	public CompilerException() {
		super();
	}

	public CompilerException(String msg) {
		super(msg);
	}

}