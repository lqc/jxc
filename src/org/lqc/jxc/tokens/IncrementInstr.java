package org.lqc.jxc.tokens;

public class IncrementInstr extends Instruction {
	
	private String id;	
	private VarDecl ref;
	private int dv;	
	private FunctionDecl action;
	
	public IncrementInstr(int l, int c, String id, int dv) {
		super(l, c);
		this.id = id;
		this.dv = dv;		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}
	
	public int getChange() {
		return dv;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the ref
	 */
	public VarDecl getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(VarDecl ref) {
		this.ref = ref;
	}

	/**
	 * @return the action
	 */
	public FunctionDecl getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(FunctionDecl action) {
		this.action = action;
	}

}
