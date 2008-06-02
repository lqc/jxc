package org.lqc.jxc.types;

import org.lqc.util.Relation;

public class IntegerType extends RealType {

	protected IntegerType(String name) {
		super(name);		
	}
	
	public Relation compareTo(Type obj) {	
		if(obj instanceof AnyType)
			return Relation.LESSER;
		
		/* obj.class >= INT */
		if(obj.getClass().isAssignableFrom(IntegerType.class))
		{
			/* obj.class <= INT */
			if(obj instanceof IntegerType)
				return Relation.EQUAL;
			
			/* obj.class > INT */
			return Relation.LESSER;			
		}
		/* obj.class < INT */
		else {
			if(IntegerType.class.isAssignableFrom(obj.getClass()))
				return Relation.GREATER;
			
			/* obj.class !~ this.class */
			return Relation.NONCOMPARABLE;			
		}		
	}
	
	public String getShorthand() 
	{
		return "I";
	}
	
}
