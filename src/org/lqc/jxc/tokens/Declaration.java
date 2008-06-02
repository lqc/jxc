package org.lqc.jxc.tokens;

import org.lqc.jxc.types.Type;
import org.lqc.util.PathID;

public abstract class Declaration<T extends Type> extends Instruction {
	
	public Declaration(int l, int c, T  t, String id) {
		super(l, c);
		
		/* XXX: this might need a fix */
		this.entityID = new PathID(id);
		this.entityType = t;		
	}
	
	protected T entityType;
	protected PathID entityID;	
	
	/**
	 * @return the entityType
	 */
	public T getType() {
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
