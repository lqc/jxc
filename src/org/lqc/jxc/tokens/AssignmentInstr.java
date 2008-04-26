package org.lqc.jxc.tokens;

public class AssignmentInstr extends Instruction {
	
	private String id;
	private Expression value;
	private VarDecl ref;
	
	public AssignmentInstr(String id, Expression e) {
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
	public String getId() {
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

}
