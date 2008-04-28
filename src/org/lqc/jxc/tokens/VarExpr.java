package org.lqc.jxc.tokens;

import org.lqc.jxc.Lexem;
import org.lqc.jxc.types.Type;

public class VarExpr extends Expression {	
	
	private String id;
	private VarDecl ref;	
	
	public VarExpr(int l, int c, String id) {
		super(l, c, Type.ANY);
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
	public void bindRef(VarDecl ref) {
		this.ref = ref;
		this.valueType = ref.getType();
	}
}
