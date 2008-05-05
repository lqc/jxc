package org.lqc.jxc.javavm;


public class InvokeInstr implements JInstr {
	
	private JType[] args;
	private int returnSize;
	private int argsSize;
	
	public InvokeInstr(JMethod method) {
		//
	}

	public int immediateArgs() {
		return 2;
	}

	public int maxStackSize() {
		return Math.max(this.args.length, 1);
	}

	public int stackChange() {		
		return (this.returnSize - this.args.length);
	}
	
	

}
