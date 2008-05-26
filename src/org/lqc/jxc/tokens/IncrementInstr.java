package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public class IncrementInstr extends Expression {
	
	private PathID id;	
	private VarDecl ref;
	private int dv;	
	private Instruction action;
	
	public IncrementInstr(int l, int c, PathID id, int dv) {
		super(l, c, Type.ANY);
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
	public PathID getId() {
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
	public Instruction getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Instruction action) {
		this.action = action;
	}

}
