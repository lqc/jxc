package org.lqc.jxc.types;

import org.lqc.util.Relation;

class AnyType extends Type {
	
	@Override
	public String toString() {
		return "<any>";
	}

	public Relation compareTo(Type object) {
		return (object.equals(VOID) ? Relation.NONCOMPARABLE : 
				Relation.EQUAL);
	}

	@Override
	public String getShorthand() {		
		return "*";
	}

}
