package org.lqc.jxc.types;

import org.lqc.util.Relation;

public class BooleanType extends PrimitiveType {

	protected BooleanType(String name) {
		super(name);	
	}

	public Relation compareTo(Type object) {
		if(object instanceof AnyType)
			return Relation.LESSER;
		
		if(object instanceof BooleanType)
			return Relation.EQUAL;
		
		return Relation.NONCOMPARABLE;
	}

}
