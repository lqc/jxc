package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;

public abstract class Declaration extends Instruction {
	
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
	
	public String toString() {
		return this.entityID + ": " + this.entityType;
	}
}
