package org.lqc.jxc.types;

public class AnyType extends Type {
	
	@Override
	public String toString() {
		return "<any>";
	}
	
	@Override
	public boolean isSupertypeOf(Type t) {
		return true;
	}
	
	

}
