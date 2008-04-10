package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public class Declaration extends Instruction {
	
	public Declaration(Type t, String id) {
		this.entityID = id;
		this.entityType = t;
	}
			
	protected Type entityType;
	protected String entityID;
	/**
	 * @return the entityType
	 */
	public Type getType() {
		return entityType;
	}
	/**
	 * @return the entityID
	 */
	public String getID() {
		return entityID;
	}
	@Override
	public void visitNode(TreeVisitor v) {
		v.visit(this);		
	}	

}
