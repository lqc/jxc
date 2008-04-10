package org.lqc.jxc.types;

public class VoidType extends Type {
	
	@Override
	public String toString() {
		return "<void>";
	}
	
	@Override
	public boolean isSupertypeOf(Type t) {
		return (t instanceof VoidType);
	}

}
