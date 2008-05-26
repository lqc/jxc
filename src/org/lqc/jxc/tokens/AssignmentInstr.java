package org.lqc.jxc.tokens;

import org.lqc.util.PathID;


public class AssignmentInstr extends Expression {
	
	private PathID id;
	private Expression value;
	private VarDecl ref;
	
	public AssignmentInstr(int l, int c, PathID id, Expression e) {
		super(l, c, e.valueType);
		this.id = id;
		this.value = e;		
	}

	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);
	}

	/**
	 * @return the id
	 */
	public PathID getId() {
		return id;
	}

	/**
	 * @return the value
	 */
	public Expression getValue() {
		return value;
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

	public void setValue(Expression e) {
		this.value = e;		
	}

}
