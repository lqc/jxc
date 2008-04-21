package org.lqc.jxc.types;

class VoidType extends Type {
	
	@Override
	public String toString() {
		return "<void>";
	}

	/** Void type is not comparable with other types. */
	public boolean isComparable(Type x) {		
		return false;
	}

	public boolean isGreaterEqual(Type x) {		
		return (x instanceof VoidType);	}

}
