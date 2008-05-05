package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;
import org.lqc.util.TriStateLogic;

public abstract class Declaration extends Instruction {
	
	public Declaration(int l, int c, Type t, String id) {
		super(l, c);
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
