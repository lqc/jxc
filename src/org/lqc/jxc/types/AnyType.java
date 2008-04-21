package org.lqc.jxc.types;

class AnyType extends Type {
	
	@Override
	public String toString() {
		return "<any>";
	}

	/* Any is comparabale with eveything except void type. */
	public boolean isComparable(Type x) {		
		return !(x instanceof VoidType);
	}

	public boolean isGreaterEqual(Type x) {
		/* Any is greater then anything */
		return true;
	}
	
	
	

}
