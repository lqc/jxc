package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public class VarExpr extends ExprToken<Type> {	
	
	private PathID id;
	private VarDecl ref;	
	
	public VarExpr(int l, int c, PathID id) {
		super(l, c, Type.ANY);
		this.id = id;	
		this.ref = null;
	}
	
	public PathID getId() { return id; }
	
	
	
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
