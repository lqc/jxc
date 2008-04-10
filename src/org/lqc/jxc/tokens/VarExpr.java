package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class VarExpr extends Expression {	
	
	private String id;
	private VarDecl ref;	
	
	public VarExpr(String id) {
		super(Type.ANY);
		this.id = id;	
		this.ref = null;
	}
	
	public String getId() { return id; }
	
	
	
	public void visitNode(TreeVisitor v) {
		v.visit(this);
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
}
