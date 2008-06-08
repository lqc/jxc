package org.lqc.jxc.types;

import org.lqc.util.Relation;

class VoidType extends Type {
	
	@Override
	public String toString() {
		return "void";
	}

	public Relation compareTo(Type object) {		
		return (object instanceof VoidType ? Relation.EQUAL : Relation.NONCOMPARABLE);
	}

	@Override
	public String getShorthand() {
		return "V";
	}

}
