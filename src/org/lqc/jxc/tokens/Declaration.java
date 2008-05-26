package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public abstract class Declaration extends Instruction {
	
	public Declaration(int l, int c, Type t, String id) {
		super(l, c);
		
		/* XXX: this might need a fix */
		this.entityID = new PathID(id);
		this.entityType = t;		
	}
	
	protected Type entityType;
	protected PathID entityID;	
	
	/**
	 * @return the entityType
	 */
	public Type getType() {
		return entityType;
	}
	
	public String getLocalID() {
		return entityID.basename();
	}
	
	public PathID getAbsoluteID() {
		return entityID;
	}
	
	public String toString() {
		return this.entityID + ": " + this.entityType;
	}
}
