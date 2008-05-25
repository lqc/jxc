package org.lqc.jxc.types;

import org.lqc.util.Relation;


public class RealType extends PrimitiveType {

	protected RealType(String name) {
		super(name);		
	}
	
	
	public Relation compareTo(Type obj) {	
		if(obj instanceof AnyType)
			return Relation.LESSER;
		
		/* obj.class >= REAL */
		if(obj.getClass().isAssignableFrom(RealType.class))
		{
			/* obj.class <= REAL */
			if(obj instanceof RealType)
				return Relation.EQUAL;
			
			/* obj.class > REAL */
			return Relation.LESSER;			
		}
		else {/* obj.class < REAL */			
			if(RealType.class.isAssignableFrom(obj.getClass()))
				return Relation.GREATER;
			
			/* obj.class !~ this.class */
			return Relation.NONCOMPARABLE;			
		}		
	}

}
