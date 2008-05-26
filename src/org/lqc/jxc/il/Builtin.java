package org.lqc.jxc.il;

import org.lqc.jxc.types.FunctionType;

public class Builtin implements Callable {
	
	protected Signature<FunctionType> signature;
	protected String contents;
	protected String branchTempl;

	public Builtin(Signature<FunctionType> sig, 
			String contents, String branchTempl) {
		this.signature = sig;
		this.contents = contents;
		this.branchTempl = branchTempl;
	}

	public Signature<FunctionType> callSignature() {
		return signature;
	}

	public StaticContainer container() {
		return null;
	}

	/**
	 * @return the contents
	 */
	public String getContents(StaticContainer dl) {
		return contents;
	}

	/**
	 * @return the branchTempl
	 */
	public String getBranchTemplate() {
		return branchTempl;
	}

}
