package org.lqc.jxc.types;

import org.lqc.util.Relation;

class VoidType extends Type {
	
	@Override
	public String toString() {
		return "<void>";
	}

	public Relation compareTo(Type object) {
		return (this.equals(object) ? Relation.EQUAL : Relation.NONCOMPARABLE);
	}

}
