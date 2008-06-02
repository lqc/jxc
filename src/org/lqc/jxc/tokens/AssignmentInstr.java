package org.lqc.jxc.tokens;

import org.lqc.jxc.transform.TreeVisitor;
import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;


public class AssignmentInstr extends ExprToken<Type> {
	
	private PathID id;
	private ExprToken<? extends Type> value;
	private VarDecl ref;
	
	public AssignmentInstr(int l, int c, PathID id, ExprToken<? extends Type> e) {
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
	public ExprToken<? extends Type> getValue() {
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

	public void setValue(ExprToken<? extends Type> e) {
		this.value = e;		
	}

}
