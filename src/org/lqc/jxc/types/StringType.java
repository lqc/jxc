package org.lqc.jxc.types;

import org.lqc.util.Relation;

public class StringType extends PrimitiveType {

	protected StringType(String name) {
		super(name);		
	}

	public Relation compareTo(Type object) {
		if(object instanceof AnyType)
			return Relation.LESSER;
		
		if(object instanceof StringType)
			return Relation.EQUAL;
		
		return Relation.NONCOMPARABLE;
	}

}
